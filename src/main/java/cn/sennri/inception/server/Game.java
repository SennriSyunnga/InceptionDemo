package cn.sennri.inception.server;

import cn.sennri.inception.Effect;
import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.field.DeckImpl;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.util.ListNode;

import java.net.InetAddress;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
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
    /**
     * 检测locks，若金库所在的层归零，则游戏返回true;
     */
    List<Integer> locks = new CopyOnWriteArrayList<>();

    Player host;

    AtomicBoolean gameResult = null;

    List<Effect> effectChain;

    public List<Effect> getEffectChain() {
        return this.effectChain;
    }

    final Player[] players;

    GameStatusEnum statusEnum;


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

    void active(String address, int playerNum, int cardNum, int effectNum, Player[] target){
        Player p = this.players[playerNum];
        if (p.getInetAddress().getHostAddress().equals(address)){
            List<Card> handCards = p.getHandCards();
            Card c = handCards.get(cardNum);
            Effect e = c.getEffect(effectNum);
            if(e.isActivable(this)){
                e.active(this, target);
            }
        }
    }

    public void revive(Player p){
        p.revive();
    }

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
