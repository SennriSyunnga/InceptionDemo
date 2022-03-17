package cn.sennri.inception.server;

import cn.sennri.inception.Effect;
import cn.sennri.inception.card.Card;
import cn.sennri.inception.client.view.FieldView;
import cn.sennri.inception.config.socket.SpringWebSocketHandler;
import cn.sennri.inception.event.*;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.field.DeckImpl;
import cn.sennri.inception.message.GameOverMessage;
import cn.sennri.inception.message.ServerStartGameMessage;
import cn.sennri.inception.message.UpdatePushMessage;
import cn.sennri.inception.message.UpdatePushMessageImpl;
import cn.sennri.inception.model.listener.Listener;
import cn.sennri.inception.player.BasePlayer;
import cn.sennri.inception.player.HostPlayer;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.player.RoleCard;
import cn.sennri.inception.util.GameUtils;
import cn.sennri.inception.util.ListNode;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Game {
    protected Deck deck = new DeckImpl();
    /**
     * 用来传递当前回合信息
     */
    protected Phase phase;

    protected ListNode<Player> pointer;

    /**
     * 秘密所在层数
     */
    protected volatile int secret = 0;

    /**
     * 检测locks，若金库所在的层归零，则游戏返回true;
     */
    protected int[] locks = new int[4];

    protected Player host;

    protected AtomicBoolean gameResult = null;

    protected List<Effect> effectChain;

    /**
     * 需要在回合阶段时结算的卡片效果，如移形换影
     */
    protected List<Effect> endPhaseEffects;

    public List<Effect> getEndPhaseEffects() {
        return endPhaseEffects;
    }

    public List<Effect> getEffectChain() {
        return this.effectChain;
    }

    protected final Player[] players;

    protected GameStatusEnum statusEnum;

    public Player getTurnOwner() {
        return turnOwner;
    }

    /**
     * 墓地列表
     */
    protected List<Card> graveyard;
    /**
     * 除外区列表
     */
    protected List<Card> exclusionZone;

    /**
     * 当前回合玩家
     */
    protected Player turnOwner;

    protected SpringWebSocketHandler handler;

    public Game(Map<WebSocketSession, String> sessionToUserMap, SpringWebSocketHandler handler) {
        this(sessionToUserMap);
        this.handler = handler;
        initializeRole();
    }

    public Game(Map<WebSocketSession, String> sessionToUserMap) {
        // 生成list以便于有序
        List<Map.Entry<WebSocketSession, String>> list = new ArrayList<>(sessionToUserMap.entrySet());
        // 随机排列站位
        Collections.shuffle(list);
        int playerSize = list.size();
        this.players = new Player[playerSize];
        Map.Entry<WebSocketSession, String> hostEntry = list.get(0);
        WebSocketSession hostSession = hostEntry.getKey();
        this.host = new HostPlayer(this, hostSession, hostEntry.getValue());
        webSocketSessionPlayerMap.put(hostSession, host);
        // 0位固定为梦主，也因此无需设置order
        players[0] = host;
        for (int i = 1; i < playerSize; i++) {
            Map.Entry<WebSocketSession, String> entry = list.get(i);
            WebSocketSession session = entry.getKey();
            Player player = new BasePlayer(this, session, entry.getValue());
            // 记录座次
            player.setOrder(i);
            webSocketSessionPlayerMap.put(session, player);
            players[i] = player;
        }
        // 构建连接关系
        this.pointer = ListNode.connectAsLoop(players);
        this.turnOwner = host;
        this.secret = 4;
        this.phase = Phase.DRAW_PHASE;
    }

    public Map<WebSocketSession, Player> webSocketSessionPlayerMap = new ConcurrentHashMap<>();

    /**
     *
     */
    public void initializeRole() {
        // 在这里sendRole信息给客户端，并等待应答

        handler.sendMessageToAllUsers(new ServerStartGameMessage(this.getView()));
    }

    /**
     * 维护一个等待结果的消息map，根据MessageId取消息。
     */
    protected Map<Long, Listener<?>> map = new ConcurrentHashMap<>();

    protected AtomicLong messageNum = new AtomicLong(0);

    /**
     * 获取消息的版本号，如果到达long极限值，则清空至0L
     * @return
     */
    private long getMessageNum() {
        return messageNum.getAndUpdate(o -> o == Long.MAX_VALUE ? 0 : o + 1);
    }

    public void setSecret(int secret) {
        this.secret = secret;
    }

    /**
     * todo 考虑放到controller层
     * @param p
     * @return
     */
    public boolean drawInDrawPhase(Player p) {
        // 这里实现hook，添加游戏事件或者触发listener
        if (p.equals(turnOwner) && phase.equals(Phase.DRAW_PHASE)) {
            p.commonDraw();
            // 结算行为
            pushUnUpdateMessage();
            // 结算抽卡引发的次生效果，这里会进行一个ask;
            takeAllEffects();
            // 这里增加一个回合切换Event 要不抽象为效果


            asking(turnOwner);
            return true;
        } else {
            // 回绝
            return false;
        }
    }

    /**
     * 游戏复活接口 考虑移到controller
     * controller → player → role → game 这样的设计？
     * @param source
     * @param targetNum
     * @param num
     * @return
     */
    public boolean revive(Player source, int targetNum, int[] num) {
        // 被复活的玩家
        Player target = players[targetNum];
        if (target.getStatus().equals(Player.StatusEnum.ALIVE)) {
            return false;
        }
        if (turnOwner != source) {
            return false;
        }
        // 这里发生次生弃牌事件
        if (source.revive(target, num)) {
            return false;
        }
        return true;
    }

    public void playerRevive(Player source, Player target) {
        unPushEvenList.add(new ReviveEvent(source.getOrder(), target.getOrder()));
    }

    public void playerAwaken(Player awakenPlayer, Player.PositionEnum positionEnum) {
        awakenPlayer.setPos(positionEnum);
        awakenPlayer.setStatus(Player.StatusEnum.ALIVE);
        unPushEvenList.add(new AwakenEvent(awakenPlayer.getOrder(), positionEnum));
    }


    /**
     * 发生击杀结果
     * @param source
     * @param target
     */
    public void shootDead(Player source, Player target) {
        target.setStatus(Player.StatusEnum.LOST);
        seizeCard(source, target, 2);
        unPushEvenList.add(new ShootDeadEvent(source.getOrder(), target.getOrder()));
    }

    /**
     * todo
     * @param source
     * @param target
     * @param num
     */
    public void seizeCard(Player source, Player target, int num) {
        int remainHandCardSize = target.getHandCards().size();
        num = Math.min(remainHandCardSize, num);
        //todo 偷卡实现
        List<Card> targetHandCards = target.getHandCards();
        List<Card> sourceHandCards = source.getHandCards();
        //
        int[] cardNumToSeize = GameUtils.getRandomAscendNumArray2(remainHandCardSize, num);
        for (int i = cardNumToSeize.length - 1; i >= 0; i--) {
            // 从大到小取牌，保证num不会变化
            Card card = targetHandCards.remove(cardNumToSeize[i]);
            sourceHandCards.add(card);
        }
        unPushEvenList.add(new SeizeCardEvent(source.getOrder(), target.getOrder(), cardNumToSeize));
    }

    protected List<Event> unPushEvenList = new ArrayList<>();

    /**
     * 本回合使用并且送去墓地的卡由该表维护
     */
    protected List<Card> usedCardInThisTurn = new ArrayList<>();

    /**
     * 发动手牌效果接口
     * @param source 效果来源
     * @param card 发动卡片
     * @param num   效果序号
     * @param targets   效果对象序号
     * @return
     */
    public boolean active(Player source, Card card, int num, int[] targets) {
        Effect e = card.getEffect(num);
        if (e.isActivationLegal(this, source, targets)) {
            // card可能是无所属卡片，因此source和owner不一定一致
            source.active(e, targets);
            effectChain.add(e);

            // 将该卡加入临时墓地区
            tempGraveyard.add(card);
            graveyard.add(card);
            // 本回合使用过的卡片增加该卡
            usedCardInThisTurn.add(card);

            ActiveEvent activeEvent = new ActiveEvent();
            activeEvent.setCardUid(card.getUid());
            activeEvent.setEffectNum(num);
            activeEvent.setObject(targets);
            activeEvent.setSubject(source.getOrder());
            activeEvent.setHandCardEffect(card.getOwner() != null);

            unPushEvenList.add(activeEvent);
            pushUnUpdateMessage();

            // 开始处于发问状态，此时会将游戏状态置为isAsking，从而拒绝其他不合法的操作
            asking(source);
            return true;
        } else {
            return false;
        }
    }

    private void active(Effect e) {
        effectChain.add(e);
    }


    /**
     * 从墓地中取牌
     * @param player
     * @param cardIndices
     */
    public void playerPickCardFromGraveyard(Player player, int[] cardIndices){
        List<Card> handCards = player.getHandCards();
        int size = graveyard.size();
        if (cardIndices.length > size){
            throw new IllegalArgumentException();
        }
        Arrays.sort(cardIndices);
        for(int i = cardIndices.length - 1; i >= 0; i--){
            int index = cardIndices[i];
            Card card = graveyard.remove(index);
            handCards.add(card);
        }
        // todo
    }

    /**
     * nums的顺序在Role的discard里做实现
     * 这里负责加even
     * @param p
     * @param nums  nums应该是一个由小到大的序列
     */
    public void discard(Player p, int[] nums) {
        List<Card> handCards = p.getHandCards();
        // 在这里加入event
        int[] cardIds = new int[nums.length];
        for (int i = nums.length - 1; i >= 0; i--) {
            int n = nums[i];
            Card c = handCards.remove(n);
            tempGraveyard.add(c);
            graveyard.add(c);
        }
        unPushEvenList.add(new DiscardEvent(p.getOrder(), cardIds));
    }

    /**
     * 结束回合 若该操作失败，则不执行该end操作并返回false;
     * @param player
     * @param discardNum 丢弃的卡牌在手卡中的编号
     */
    public boolean endTurn(Player player, int[] discardNum) {
        if (this.turnOwner.equals(player) && this.phase.equals(Phase.USE_PHASE)) {
            int disNum = discardNum.length;
            List<Card> handCards = player.getHandCards();
            int size = handCards.size();
            // 验证弃牌数量是否正确
            if (size - disNum > 5) {
                return false;
            }
            // 判断是否要弃牌，不弃牌则不读参数
            if (size > 5) {
                boolean success = player.discard(discardNum);
                if (!success) {
                    return false;
                } else {
                    DiscardEvent event = new DiscardEvent();
                    event.setSubject(player.getOrder());
                    int[] cardIds = new int[discardNum.length];
                    for (int i = 0; i < discardNum.length; i++) {
                        cardIds[i] = handCards.get(discardNum[i]).getUid();
                    }
                    event.setCardIds(cardIds);
                    unPushEvenList.add(event);
                }
            }
            // 进入结束阶段
            this.phase = Phase.END_PHASE;
            // 添加到 队列 最前面
            PhaseEndEvent endEvent = new PhaseEndEvent();
            // 服务端的evenList不关心顺序，只关心历史
            unPushEvenList.add(0, endEvent);
            pushUnUpdateMessage();

            // 结算效果 比如移形换影在这时候结算 这里通过交换来避免构建新的list
            List<Effect> temp = effectChain;
            effectChain = endPhaseEffects;
            endPhaseEffects = temp;
            temp = null;
            takeAllEffects();
            // 易知由于移形换影效果会把自己再次添加到endPhaseEffect栈中
            endPhaseEffects.clear();

            // 重置当前所有玩家的角色卡计数等回合内信息
            for (Player p : players) {
                p.refreshItsRole();
            }

            // 这里开始不ask，直接结算。
            pointer = pointer.next;
            turnOwner = pointer.getNode();
            this.phase = Phase.DRAW_PHASE;

            // 推送回合结束事件
            unPushEvenList.add(new TurnEndEvent());
            pushUnUpdateMessage();
            eventList.clear();
            usedCardInThisTurn.clear();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从卡顶弃牌
     * @param times 弃牌次数
     */
    public void deckAbandonCard(int times) {
        int[] cardIds = new int[times];
        for (int i = 0; i < times; i++) {
            if (deck.size() == 0) {
                gameOver(true);
            }
            Card card = deck.remove();
            graveyard.add(card);
            tempGraveyard.add(card);
            cardIds[i] = card.getUid();
        }
        unPushEvenList.add(new AbandonEvent(cardIds));
    }

    /**
     *
     * @param player 抽卡玩家
     * @param drawTimes 抽卡次数
     */
    public void drawDeck(Player player, int drawTimes) {
        for (int i = 0; i < drawTimes; i++) {
            if (deck.size() == 0) {
                gameOver(true);
            }
            Card card = this.deck.draw();
            player.getHandCards().add(card);
        }
        DrawEvent drawEvent = new DrawEvent();
        unPushEvenList.add(drawEvent);
    }

    /**
     * 游戏结束调用这个
     * @param hostPlayerWin 游戏结束时是否梦主获胜
     */
    public void gameOver(boolean hostPlayerWin) {
        handler.sendMessageToAllUsers(new GameOverMessage(hostPlayerWin));
    }

    /**
     * 从某个地方除外卡片
     * @param card
     * @param from 需要移除卡片的区域，可以是墓地、
     */
    public void vanish(Card card, List<Card> from) {
        from.remove(card);
        exclusionZone.add(card);
    }

    /**
     * asking主循环结束后可以执行
     * @param askingPlayer
     */
    public void asking(Player askingPlayer) {
        // 正在询问的指针移动指向当前玩家, 如果当前玩家为被询问玩家，则两个指针此时同时指向同一个玩家
        this.askingPlayer = playerListNodeMap.get(askingPlayer);
        // 若已经处于询问递归当中，说明此时这不是最外层的问询，则什么也不做。
        if (!isAsking.get()) {
            isAsking.set(true);
            // 这里有必要是异步的吗？
            Future<?> future = handler.asyncThreadPoolTaskExecutor.submit(() -> {
                // 主循环
                try {
                    askedPlayer = this.askingPlayer.next;
                    AskingEvent askingEvent = new AskingEvent(askingPlayer.getOrder(), askedPlayer.getNode().getOrder());
                    unPushEvenList.add(askingEvent);
                    pushUnUpdateMessage();
                    while (isAsking.get()) {
                        // 应当在此等待对方应答或者发动效果。
                        waitAnswer();
                    }
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                }
                // 此时说明asking和其延伸问询全部结束，此时应该清算结果
                takeAllEffects();
            });
        } else {
            // 这一次的asking不会开启新的循环，只会使得上一个循环中的await被解冻
            answerIfAsking();
        }
    }

    /**
     * 可重用的锁
     */
    public CyclicBarrier answer = new CyclicBarrier(2);

    public void waitAnswer() throws BrokenBarrierException, InterruptedException {
        // 等待触发
        answer.await();
        pushUnUpdateMessage();
    }

    /**
     * 若当前有阻塞的waitAnswer，则响应该Answer
     * 该方法仅在两种情况下被调用：
     * 1.{@link #asking(Player)}在询问环节中，发起了新的Asking；
     * 2.{@link #pass()}在询问环节中，不进行回应。
     */
    public synchronized void answerIfAsking() {
        // 判断是否正在询问
        if (answer.getNumberWaiting() == 1) {
            // 将被询问对象指向自己接下来的用户
            try {
                // 易知，若该方法被Askng调用，此时Asking和Asked是重合的, 指针在这里没有区别, 而若被pass调用，asked就应当正常向后走一格。
                ListNode<Player> next = askedPlayer.next;
                // 改变指针指向
                askedPlayer = next;
                AskingEvent askingEvent = new AskingEvent(askingPlayer.getNode().getOrder(), askedPlayer.getNode().getOrder());
                unPushEvenList.add(askingEvent);
                answer.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 当前用户放弃响应操作
     */
    public void pass() {
        if (!isAsking.get()) {
            throw new IllegalStateException("当前并未进行询问");
        } else {
            // 若放弃该操作的玩家是当前asking玩家，易知，此时整轮玩家都放弃了操作
            if (askedPlayer == askingPlayer) {
                // asked玩家是最后一个需要响应的玩家，他也pass时，说明整个asking环节已经结束了。
                isAsking.set(false);
            } else {
                answerIfAsking();
            }
        }
    }

    /**
     * 具有响应权的玩家
     */
    protected volatile ListNode<Player> askedPlayer;

    protected volatile ListNode<Player> askingPlayer;

    protected Map<Player, ListNode<Player>> playerListNodeMap = new HashMap<>();

    protected volatile List<Event> eventList;

    protected AtomicBoolean isAsking = new AtomicBoolean(false);

    /**
     * 在这里会清理EventList
     * 设计上，不会同时执行同步块中的方法，但是可能由于askingHost造成递归。当然，askingHost是异步的，
     * asking若是异步的，会不会导致takeAllEffect在某个调用中过早结束而发生误判，最终出错？
     */
    protected void takeAllEffects() {
        if (!effectChain.isEmpty() || !tempGraveyard.isEmpty() || !eventList.isEmpty()) {
            int lastChainIndex = effectChain.size() - 1;
            // 对象上锁
            synchronized (this) {
                // 该由这一组event引发的效果已经发动完了，应当结算掉这组event
                eventList.clear();
                // todo send clean message

                // 若不为空
                while (lastChainIndex > 0) {
                    Effect e = effectChain.remove(lastChainIndex);
                    // 效果结算, 在这里头添加各种event
                    e.takeEffect(this);
                    // 推送更新数据到客户端 增加新的Event到eventList
                    pushUnUpdateMessage();
                    lastChainIndex--;
                }
                // 检查墓地效果起效， 比如弃牌阶段诱发的时间风暴在这个时候结算
                int tempGraveyardSize = tempGraveyard.size();
                while (tempGraveyardSize > 0) {
                    // 将暂存区的这张卡去除
                    Card c = tempGraveyard.remove(0);
                    // 只当是时间风暴
                    if (c.isActivable(this)) {
                        active(c.getEffect(0));
                    }
                    tempGraveyardSize--;
                }
                // 此处清空墓地缓存
            }
            // 此时可能由新的takeEffect导致evenList堆积，或者添加了次生的效果
            asking(host);
            // 这里是不是应该阻塞等待asking结束？
        }
    }

    /**
     * 临时墓地区域,该区域代表在一组连锁上将要送去墓地的卡片，包括：发动效果而送墓的卡、因为cost而送墓的卡
     */
    protected List<Card> tempGraveyard = new CopyOnWriteArrayList<>();

    public List<Card> getTempGraveyard() {
        return tempGraveyard;
    }

    public List<Card> getGraveyard() {
        return graveyard;
    }

    public List<Card> getExclusionZone() {
        return exclusionZone;
    }

    /**
     * 移形换影交换角色
     * @param here
     * @param there
     * @return
     */
    public boolean switchRole(Player here, Player there) {
        if (there == host) {
            return false;
        } else {
            // 这里的实现不完全正确;但是由于host角色不存在非回合方的效果，shoot行为不使用role实现，回合外效果主要通过场地卡来执行，因此暂且保留这个实现。
            RoleCard r = here.getRole();
            here.setRole(there.getRole());
            there.setRole(r);
        }
        return true;
    }


    /**
     * 解锁接口，在这里添加解锁event
     * @param layerNum
     */
    public void decryptLock(int layerNum) {
        if (locks[layerNum] == 0) {
            throw new IllegalArgumentException("");
        } else {
            locks[layerNum]--;
            if (locks[layerNum] == 0) {
                if (layerNum == secret) {
                    // notify GameStop
                } else {
                    // notify NightMare
                }
            }
        }
    }

    public void start() {
        this.statusEnum = GameStatusEnum.PLAYING;
    }

    // 推出尚未
    public void pushUnUpdateMessage() {
        pushUpdateMessage(unPushEvenList);
        eventList.addAll(unPushEvenList);
        unPushEvenList.clear();
    }

    private void pushUpdateMessage(UpdatePushMessage message) {
        handler.sendMessageToAllUsers(message);
    }

    /**
     *
     * @param events    非null,如果推送unPushMessage则event List肯定实例化了
     */
    public void pushUpdateMessage(@NotNull List<Event> events) {
        if (events.size() > 0) {
            UpdatePushMessage updatePushMessage = new UpdatePushMessageImpl();
            updatePushMessage.setEventList(events);
            pushUpdateMessage(updatePushMessage);
        }
    }

    public Phase getPhase() {
        return phase;
    }

    public ListNode<Player> getPointer() {
        return pointer;
    }

    public int getSecret() {
        return secret;
    }

    public int[] getLocks() {
        return locks;
    }

    public Player getHost() {
        return host;
    }

    public AtomicBoolean getGameResult() {
        return gameResult;
    }

    public Player[] getPlayers() {
        return players;
    }

    public GameStatusEnum getStatusEnum() {
        return statusEnum;
    }

    public Map<Long, Listener<?>> getMap() {
        return map;
    }

    public CyclicBarrier getAnswer() {
        return answer;
    }

    public ListNode<Player> getAskedPlayer() {
        return askedPlayer;
    }

    public ListNode<Player> getAskingPlayer() {
        return askingPlayer;
    }

    public Map<Player, ListNode<Player>> getPlayerListNodeMap() {
        return playerListNodeMap;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public AtomicBoolean getIsAsking() {
        return isAsking;
    }

    public Deck getDeck() {
        return deck;
    }

    public enum GameStatusEnum {
        WAITING,
        PLAYING,
    }

    public enum Phase {
        /**
         * 抽卡阶段
         */
        DRAW_PHASE,
        /**
         * 出牌阶段
         */
        USE_PHASE,
        /**
         * 结束阶段，此时处理弃牌、结束阶段效果
         */
        END_PHASE;
        final static EnumMap<Phase, Phase> NEXT_MAP = new EnumMap<>(Phase.class);

        static {
            NEXT_MAP.put(DRAW_PHASE, USE_PHASE);
            NEXT_MAP.put(USE_PHASE, END_PHASE);
            NEXT_MAP.put(END_PHASE, DRAW_PHASE);
        }

        public Phase toNext() {
            return toNextPhase(this);
        }

        public static Phase toNextPhase(Phase current) {
            return NEXT_MAP.get(current);
        }
    }

    private FieldView getView() {
        return new FieldView(this);
    }

}
