package cn.sennri.inception.server;

import cn.sennri.inception.Effect;
import cn.sennri.inception.card.Card;
import cn.sennri.inception.event.Event;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.field.DeckImpl;
import cn.sennri.inception.model.listener.Listener;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.player.Role;
import cn.sennri.inception.util.ListNode;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Game {
    protected final Deck deck = new DeckImpl();
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

    // Player里应该维护一个socket

    protected Player turnOwner;


    public Game(List<InetAddress> list) {
        // 抽取梦主
        Collections.shuffle(list);
        Deck deck = new DeckImpl();
        deck.shuffle();
        secret = 4;
        phase = Phase.DRAW_PHASE;
        int playerSize = list.size();
        players = new Player[playerSize];
        host = players[0];
        turnOwner = host;
        initialize();
    }

    public void initialize() {
        // 根据人数 处理list，形成游戏布局

        // 有环对象随机抽取梦主
        // future 异步等待选择结果
        // future.get() 获取角色选择信息
        // 随机构成环
        // 这里应该替换成根据当前人数来从Factory获取对应的deckImpl

        //pointer 指针指向当前玩家
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

    public boolean drawInDrawPhase(Player p) {
        // 这里实现hook，添加游戏事件或者触发listener
        if (p.equals(turnOwner) && phase.equals(Phase.DRAW_PHASE)) {
            p.commonDraw(deck);
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
     * 游戏复活接口
     * @param source
     * @param targetNum
     * @param num
     * @return
     */
    public boolean revive(Player source, int targetNum, int[] num) {
        Player target = players[targetNum];
        if (target.getStatus().equals(Player.StatusEnum.ALIVE)) {
            return false;
        }
        if (turnOwner != source) {
            return false;
        }
        return source.revive(target, num);
    }

    /**
     *  todo 不一定存在效果对象
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
            return true;
        } else {
            return false;
        }
    }

    private void active(Effect e) {
        effectChain.add(e);
    }


    /**
     * nums的顺序在Role的discard里做实现
     * 这里负责加even
     * @param p
     * @param nums
     */
    public void discard(Player p, int[] nums) {
        List<Card> handCards = p.getHandCards();
        // 在这里加入event
        for (int n : nums) {
            Card c = handCards.remove(n);
            tempGraveyard.add(c);
            graveyard.add(c);
        }
    }

    /**
     * 结束回合 若该操作失败，则回卷
     * @param player
     * @param discardNum 丢弃的卡牌在手卡中的编号
     */
    public boolean endTurn(Player player, int[] discardNum) {
        if (this.turnOwner.equals(player) && this.phase.equals(Phase.USE_PHASE)) {
            int disNum = discardNum.length;
            int size = player.getHandCards().size();
            // 验证弃牌数量是否正确
            if (size - disNum > 5) {
                return false;
            }
            // 判断是否要弃牌
            if (size > 5) {
                boolean success = player.discard(discardNum);
                if (!success) {
                    return false;
                }
            }
            // 进入结束阶段
            this.phase = Phase.END_PHASE;
            pushView();
            // 结算效果 比如移形换影在这时候结算
            takeAllEffects();

            turnOwner.refreshItsRole();
            pointer = pointer.next;
            turnOwner = pointer.getNode();
            this.phase = Phase.DRAW_PHASE;
            pushView();
            return true;
        } else {
            return false;
        }
    }

    public void deckAbandonCard(int times) {
        for (int i = 0; i < times; i++) {
            deck.abandon(graveyard, tempGraveyard);
        }
    }

    public void vanish(Card card, List<Card> from) {
        from.remove(card);
        exclusionZone.add(card);
    }

    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

    {
        taskExecutor.setCorePoolSize(20);
        taskExecutor.setMaxPoolSize(200);
        taskExecutor.setQueueCapacity(25);
        taskExecutor.setKeepAliveSeconds(200);
        taskExecutor.setThreadNamePrefix("oKong-");
        // 线程池对拒绝任务（无线程可用）的处理策略，目前只支持AbortPolicy、CallerRunsPolicy；默认为后者
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
    }

    /**
     * asking主循环结束后可以执行
     * @param askingPlayer
     */
    public void asking(Player askingPlayer) {
        // 改变指针指向
        this.askingPlayer = playerListNodeMap.get(askingPlayer);
        // 若已经处于询问递归当中，说明此时这不是最外层的问询，则什么也不做。
        if (!isAsking.get()) {
            isAsking.set(true);
            Future<?> future = taskExecutor.submit(() -> {
                // 主循环
                try {
                    askedPlayer = this.askingPlayer;
                    while (isAsking.get()) {
                        // 更新信息
                        pushView();
                        // 应当在此等待对方应答或者发动效果。
                        waitAnswer();
                    }
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                }
                // asking和其延伸问询全部结束
                takeAllEffects();
            });
        } else {
            answerIfAsking();
        }
    }

    /**
     * 可重用
     */
    public CyclicBarrier answer = new CyclicBarrier(2);

    public void waitAnswer() throws BrokenBarrierException, InterruptedException {
        answer.await(); // 等待触发
    }

    /**
     * 若当前有阻塞的waitAnswer，则响应该Answer
     */
    public synchronized void answerIfAsking() {
        if (answer.getNumberWaiting() == 1) {
            try {
                ListNode<Player> next = askedPlayer.next;
                askedPlayer = next;
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
            // 若放弃该操作的玩家是当前asking玩家，说明此时整轮玩家都放弃了操作
            if (askedPlayer == askingPlayer) {
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

    // 不会同时执行多个以下方法
    // 不会在这个过程中有新的卡加入该卡池。
    // 该过程执行完一定会清空效果池
    protected void takeAllEffects() {
        while (!effectChain.isEmpty() || !tempGraveyard.isEmpty() || !eventList.isEmpty()) {
            int lastChainIndex = effectChain.size() - 1;
            // 对象上锁
            synchronized (this) {
                // 若不为空
                while (lastChainIndex > 0) {
                    Effect e = effectChain.remove(lastChainIndex);
                    // 效果结算
                    e.takeEffect(this);
                    // 推送更新数据到客户端
                    pushView();
                    // event?
                    lastChainIndex--;
                }
                // 检查墓地效果起效 移形换影应该在这个时候结算
                int tempGraveyardSize = tempGraveyard.size();
                while (tempGraveyardSize > 0) {
                    Card c = tempGraveyard.remove(0);
                    // 只当是时间风暴
                    if (c.isActivable(this)) {
                        active(c.getEffect(0));
                    }
                    // 这个效率很低
                    tempGraveyardSize--;
                }
            }
            // 开始新一轮响应 这个过程可能添加新的effect进入effectChain
            asking(host);
            // 响应结束或者无响应
            eventList.clear();
        }
    }

    /**
     * 临时墓地区域
     */
    protected List<Card> tempGraveyard = new CopyOnWriteArrayList<>();

    /**
     * 出牌区域
     */
    protected List<Card> playArea;


    public List<Card> getTempGraveyard() {
        return tempGraveyard;
    }

    public List<Card> getPlayArea() {
        return playArea;
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
            Role r = here.getRole();
            here.setRole(there.getRole());
            there.setRole(r);
        }
        return true;
    }


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


//    public void initialize() {
//        // 根据人数 处理list，形成游戏布局
//
//        roles = new CopyOnWriteArrayList<>();
//
//        // 分发角色牌
//        int size = list.size();
//        CompletableFuture[] futures = new CompletableFuture[size];
//
//        for (int i = 1; i < size; i++) {
//            // 从角色卡中抽出几张牌当做角色
//            int finalI = i;
//            roles.add(null);
//            CompletableFuture<String> res = CompletableFuture.supplyAsync(() -> {
//                // remote
//                while (true) {
//                    // await
//                    String roleName = roles.get(finalI);
//                    if (roleName != null) {
//                        return roleName;
//                    }
//                }
//            });
//            futures[i] = res;
//        }
//        CompletableFuture.allOf(futures);
//        roles = null;
//        players = new Player[size];
//
//        try {
//            players[0] = getNewPlayer((String) futures[0].get(), list.get(0), null);
//            for (int i = 0; i < size; i++) {
//                CompletableFuture<String> future = futures[i];
//                players[i] = getNewPlayer(future.get(), list.get(i + 1), null);
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//
//
//        // 有环对象随机抽取梦主
//        // future 异步等待选择结果
//        // future.get() 获取角色选择信息
//        // 随机构成环
//        // 这里应该替换成根据当前人数来从Factory获取对应的deckImpl
//        Deck deck = new DeckImpl();
//        deck.shuffle();
//        secret = 4;
//        phase = Phase.DRAW_PHASE;
//        host = players[0];
//        turnOwner = host;
//        //pointer 指针指向当前玩家
//    }

    /**
     * @return if host wins
     */
    public void start() throws BrokenBarrierException, InterruptedException, ExecutionException {
        // 应该为守护线程
        this.statusEnum = GameStatusEnum.PLAYING;
    }

    public void pushView() {

    }
    // 根据playerClient里的card信息，发送指令给playerServer
    // 服务器校验阶段是否正确。
    // 服务器校验效果是否正确
    // 构建效果栈，push该效果
    // 按逆时针顺序同步询问是否响应。
    // 对手玩家选择响应卡。
    // 若有响应，加入效果栈。
    // 该过程为一个while true 过程。
    // 维护一个询问指针，逆时针走动，维护一个最后一个发动效果玩家的引用。
    // 若发动效果，将最后发动玩家置为该玩家，
    // 若当前玩家不发动效果，则检查当前玩家是否为上轮发动的玩家
    // 若相同则结束循环，开始pop效果，依次处理。
    // 每次循环结束时询问指针向后移动

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

    public ThreadPoolTaskExecutor getTaskExecutor() {
        return taskExecutor;
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
        ASKING,
        END
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


}
