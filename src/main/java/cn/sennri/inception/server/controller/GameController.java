package cn.sennri.inception.server.controller;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.player.BasePlayer;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sennri
 */
@RequestMapping("/game")
@RestController
public class GameController {

    @Autowired
    WebSocketHandler webSocketHandler;

    private static final Logger logger = LogManager.getLogger(GameController.class.getName());
    Game game;

    CyclicBarrier gameEndBarrier;

    AtomicInteger deck = new AtomicInteger(1);

    public GameController() throws UnknownHostException {
    }

    Player testPlayer = new BasePlayer(InetAddress.getLocalHost());

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public void test() throws BrokenBarrierException, InterruptedException {
        logger.info("game start");
    }


    final static int MIN_PLAYER = 2;
    final static int MAX_PLAYER = 3;
    ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_PLAYER, MAX_PLAYER, 10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10),
            new ThreadPoolExecutor.DiscardPolicy());

    List<Player> playerList;

    AtomicBoolean lobbyBusy;

    final Map<InetAddress, AtomicBoolean> addressMap = new ConcurrentHashMap<>(MAX_PLAYER);

    /**
     * 应该设置为异步方法
     */
    void createLobby(){
        if (lobbyBusy != null){
            logger.warn("Lobby already exists");
            return;
        }
        lobbyBusy = new AtomicBoolean(false);
        // 若没有被quit
        while (lobbyBusy != null){
            boolean allClear = false;
            while (!allClear){
                allClear = true;
                for(AtomicBoolean ready:addressMap.values()){
                    if (!ready.get()){
                        allClear = false;
                        break;
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            lobbyBusy.set(true);

            List<InetAddress> list = new ArrayList<>(addressMap.keySet());

            game = new Game(list);
            CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    try {
                        game.start();
                    } catch (BrokenBarrierException | InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    lobbyBusy.set(false);
                }
            });
        }
    }

    void active(String address, int playerNum, int cardNum){
        Player p = this.playerList.get(playerNum);
        if (p.getInetAddress().getHostAddress().equals(address)){
            List<Card> handCards = p.getHandCards();

        }
    }

    /**
     * 进入房间
     */
    void enterLobby(String ipAddress) throws UnknownHostException {
        InetAddress ip = InetAddress.getByName(ipAddress);
        if (lobbyBusy != null && !lobbyBusy.get()){
            if (addressMap.put(ip, new AtomicBoolean(false)) == null){
                push();
            }
        }
    }

    /**
     * 这里应该是在handle里处理的，接收到closing事件时检查是否直接终止游戏。
     * 退出逻辑应该也在socket做实现
     * 某个用户退出房间
     * 设计上应该在等待阶段
     */
    void quitLobby(String ipAddress){
        InetAddress ip;
        try {
            ip = InetAddress.getByName(ipAddress);
            if (addressMap.remove(ip) != null){
                // 如果是在游戏中退出的话
                lobbyBusy.getAndSet(false);
                push();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出房间
     * 设计上应该在等待阶段
     */
    void dissolveLobby(){
        lobbyBusy = null;
        addressMap.clear();
        playerList = null;
        push();
    }


    /**
     * 游戏终止信号
     * 当前发动的卡信息（一个stack）
     * 当前回合玩家
     * 是否为当前响应玩家
     * 效果发动、效果结算、回合转移时、响应玩家转移时、玩家主动离开时、应当push
     * push的是事件的发生或者事件的结果。
     * 因此push应该分为几种类型：宣告发生帧，结果告知帧。
     * 在每个push完成后，需要依次进行askclient，判断是否需要
     */
    void push(){




    }




    /**
     * 向client依次发出请求，确认其是否应答效果
     */
    void askClient(){

    }

}
