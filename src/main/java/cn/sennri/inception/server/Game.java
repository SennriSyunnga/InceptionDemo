package cn.sennri.inception.server;

import cn.sennri.inception.field.Deck;
import cn.sennri.inception.field.DeckImpl;
import cn.sennri.inception.player.HostPlayer;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.util.ListNode;

import java.net.InetAddress;
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
    /**
     * 用于在每一次动作之后监测是否结束游戏
     */
    final CyclicBarrier actionBarrier = new CyclicBarrier(3);

    /**
     * 用于切换phase
     */
    final CyclicBarrier phaseBarrier = new CyclicBarrier(2);

    CountDownLatch deckIsEmpty = new CountDownLatch(1);

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


    public Game(List<InetAddress> list) {
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
//               return "BasePlayer";
            });
            futures[i - 1] = res;
        }
        CompletableFuture.allOf(futures);
        roles = null;
        Player[] ans = new Player[size - 1];
        // todo 可以整合到supplyAsync里头
        try {
            for (int i = 0; i < size; i++) {
                CompletableFuture<String> future = futures[i];
                ans[i] = getNewPlayer(future.get(), list.get(i + 1), null);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        initialize();
    }

    public void pushHostCard(InetAddress host) {
        // 异步抽选host卡发送给host

    }

    List<String> roles;

    public void pushGuestCard(List<InetAddress> list) {

    }


    public void draw(){

    }

    public void revive(){

    }

//    /**
//     * 等待加入，构成players 等待ready状态。
//     */
//    public void waitCustomer() throws InterruptedException {
//        // 等待主机连结;
//        // 等待结束条件： 达到人数上限 或 达到人数下限且玩家均已准备
//        while (playerNum.get() < MIN_PLAYER) {
//
//            // 并发list接受多线程的playerList添加
//            // 计时器？
//        }
//        // 若满足最小玩家条件，则进入下面的条件
//        // 这是异步向下
//
//        while (!isReady(playerList)) {
//            // 等待时间
//        }
//        //
//        //
//        // 方法返回条件：玩家均已准备
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
    public boolean start() throws BrokenBarrierException, InterruptedException, ExecutionException {
        // 应该为守护线程
        CompletableFuture.runAsync(() -> {
            try {
                while (gameResult == null) {
                    Player current = pointer.getNode();
                    // 等待抽卡阶段结束
                    phaseBarrier.await();
                    phase = phase.toNext();

                    // 此时是出牌阶段，等待出牌阶段结束
                    actionBarrier.await();
                    phase = phase.toNext();


                    // 此时是结束阶段，等待结束阶段结束
                    actionBarrier.await();
                    phase = phase.toNext();
                    // 切换当前用户
                    pointer = pointer.getNext();
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        });


        CompletableFuture<Boolean> hostWin = CompletableFuture.supplyAsync(() -> {
            while (true) {
                try {
                    actionBarrier.await();
                    if (deck.isEmpty()) {
                        return true;
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });
        CompletableFuture<Boolean> guestWin = CompletableFuture.supplyAsync(() -> {
            while (true) {
                try {
                    actionBarrier.await();
                    if (locks.get(secret) == 0) {
                        return false;
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });
        Boolean result = null;
        try {
            result = (Boolean) CompletableFuture.anyOf(hostWin, guestWin).get();
            return result;
        } catch (ExecutionException e) {
            throw e;
        }
    }

    public void stepOn() throws BrokenBarrierException, InterruptedException {
        this.actionBarrier.await();
    }

    //    public boolean start() throws BrokenBarrierException, InterruptedException {
//        initialize();
//
//        while (true) {
//            Player current = pointer.getNode();
//
//            // 等待current发动效果
//
//
//            // 当前回合结束
//            barrier.await();
//            if(deck.isEmpty()){
//                return true;
//            }else if (locks[secret] == 0){
//                return false;
//            }
//            // 切换当前用户
//            pointer = pointer.getNext();
//        }
//    }
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
