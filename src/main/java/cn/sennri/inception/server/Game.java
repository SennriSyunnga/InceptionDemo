package cn.sennri.inception.server;

import cn.sennri.inception.Effect;
import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.field.DeckImpl;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.util.ListNode;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static cn.sennri.inception.player.PlayerFactory.getNewPlayer;

public class Game {
    private final Deck deck = new DeckImpl();
    /**
     * 用来传递当前回合信息
     */
    Phase phase;

    ListNode<Player> pointer;

    /**
     * 秘密所在层数
     */
    volatile int secret = 0;
//    /**
//     * 检测locks，若金库所在的层归零，则游戏返回true;
//     */
//    List<Integer> locks = new CopyOnWriteArrayList<>();

    /**
     * 检测locks，若金库所在的层归零，则游戏返回true;
     */
   int[] locks = new int[4];

    Player host;

    AtomicBoolean gameResult = null;

    List<Effect> effectChain;

    public List<Effect> getEffectChain() {
        return this.effectChain;
    }

    final Player[] players;

    GameStatusEnum statusEnum;

    /**
     * 墓地列表
     */
    List<Card> graveyard;
    /**
     * 除外区列表
     */
    List<Card> exclusionZone;


    public Game(List<InetAddress> list) {
        Collections.shuffle(list);

        // 根据人数 处理list，形成游戏布局

        roles = new CopyOnWriteArrayList<>();

        // 分发角色牌
        int size = list.size();
        CompletableFuture[] futures = new CompletableFuture[size];

        for (int i = 1; i < size; i++) {
            // 从角色卡中抽出几张牌当做角色
            int finalI = i;
            roles.add(null);
            CompletableFuture<String> res = CompletableFuture.supplyAsync(() -> {
                // remote
                while (true) {
                    // await
                    String roleName = roles.get(finalI);
                    if (roleName != null) {
                        return roleName;
                    }
                }
            });
            futures[i] = res;
        }
        CompletableFuture.allOf(futures);
        roles = null;
        players = new Player[size];

        try {
            players[0] = getNewPlayer((String)futures[0].get(), list.get(0), null);
            for (int i = 0; i < size; i++) {
                CompletableFuture<String> future = futures[i];
                players[i] = getNewPlayer(future.get(), list.get(i + 1), null);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        initialize();
    }

    void setSecret(int secret){
        this.secret = secret;
    }

    public void pushHostCard(InetAddress host) {
        // 异步抽选host卡发送给host

    }

    List<String> roles;

    public Deck getDeck() {
        return deck;
    }

    public void pushGuestCard(List<InetAddress> list) {

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


    void active(String address, int playerNum, int cardNum, int effectNum, Player[] target){
        Player p = this.players[playerNum];
        if (p.getInetAddress().getHostAddress().equals(address)){
            List<Card> handCards = p.getHandCards();
            Card c = handCards.get(cardNum);
            Effect e = c.getEffect(effectNum);
            if(e.isActivable(this)){
                e.active(this, target);

                taskExecutor.submit(() -> {
                    while (!effectChain.isEmpty()){
                        // asking

                    }
                });
                //这里submit一个异步线程。进行后续的处理
            }
        }
    }

    /**
     * 具有响应权的玩家
     */
    Player AskedPlayer;

    Map<Player, ListNode<Player>> playerListNodeMap = new HashMap<>();

    void asking(Player p){
        statusEnum = GameStatusEnum.ASKING;
        ListNode<Player> asking = playerListNodeMap.get(p);
        ListNode<Player> asked = asking.next;
        while (!asked.getNode().equals(p)){
            sendAsk();// 应当触发推送trigger

            // 等待时长

            //

            asked = asked.getNext();
        }
        // 将asked 置为下一顺位玩家。
        // 将asking玩家置为当前玩家
        //
//        if ()
//        statusEnum = GameStatusEnum.PLAYING;
    }

    void sendAsk(){}

    /**
     * 临时墓地区域
     */
    List<Card> tempGraveyard;

    /**
     * 出牌区域
     */
    List<Card> playArea;

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

    void decryptLock(int layerNum){
        if (locks[layerNum] == 0){
            throw new IllegalArgumentException("");
        }else{
            locks[layerNum]--;
            if (locks[layerNum] == 0){
                if (layerNum == secret){
                    // notify GameStop
                }else{
                    // notify NightMare
                }
            }
        }
    }

    //    public void revive(Player p){
//        p.revive();
//    }

    public void initialize() {
        // 有环对象随机抽取梦主
        // future 异步等待选择结果
        // future.get() 获取角色选择信息
        // 随机构成环
        // 这里应该替换成根据当前人数来从Factory获取对应的deckImpl
        Deck deck = new DeckImpl();
        deck.shuffle();
        secret = 4;
        phase = Phase.DRAW_PHASE;

        //pointer 指针指向当前玩家
    }

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
