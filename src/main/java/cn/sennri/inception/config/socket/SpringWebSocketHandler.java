package cn.sennri.inception.config.socket;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.message.*;
import cn.sennri.inception.model.listener.Listener;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;
import cn.sennri.inception.server.GameFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


public class SpringWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    public ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(SpringWebSocketHandler.class);

    /**
     * Map来存储WebSocketSession，key用USER_ID 即在线用户列表
     */
    private static final Map<String, WebSocketSession> usersToSessionMap;

    private static final Map<WebSocketSession, String> sessionToUserMap;

    private static final Set<WebSocketSession> readySessionSet;

    /**
     * 用户自定义标识 对应监听器从的key
     */
    private static final String USER_ID = "WEB_SOCKET_USERID";

    /**
     * 维护一个等待结果的消息map，根据MessageId取消息。
     */
    Map<Long, Listener<?>> listenerHookMap = new ConcurrentHashMap<>();

    /**
     * 指向本机的socket，大厅创建时保留该引用
     */
    private WebSocketSession hostSocket;

    /**
     * 大厅处于激活状态,默认处于关闭装填
     */
    private final AtomicBoolean lobbyCreated = new AtomicBoolean(false);

    private final AtomicBoolean isPlaying = new AtomicBoolean(false);


    static {
        usersToSessionMap = new ConcurrentHashMap<>();
        sessionToUserMap = new ConcurrentHashMap<>();
        readySessionSet = ConcurrentHashMap.newKeySet();
    }

    public SpringWebSocketHandler() {
    }

    @Autowired
    public ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor;

    /**
     * 连接成功时候，会触发页面上onopen方法
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        if (Objects.requireNonNull(session.getRemoteAddress().getAddress()).equals(session.getLocalAddress().getAddress())) {
            this.hostSocket = session;
            logger.info("用户为本机用户，正在创建大厅。");
            lobbyCreated.set(true);
        } else {
            // 若大厅未建立，则拒绝非本机的连接
            if (!lobbyCreated.get()) {
                try {
                    sendMessage(session, new ErrorMessage("当前主机未建立大厅"));
                    session.close(CloseStatus.POLICY_VIOLATION);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        InetAddress inetAddress = Optional.ofNullable(session.getRemoteAddress()).orElseThrow(NullPointerException::new).getAddress();
        logger.debug("WebSocket成功建立与{}的连接!", inetAddress);
        // 若非空，则携带信息，当前为测试，因此不强制要求携带信息；
        Optional.ofNullable(session.getHandshakeHeaders().get(USER_ID)).ifPresent(o -> {
            String name = o.get(0);
            logger.debug("会话{}将自身标识为{}, 将使用该身份作为唯一标识", inetAddress, name);
            usersToSessionMap.put(name, session);
            sessionToUserMap.put(session, name);
            logger.debug("将{}登记为用户{}", inetAddress, name);
        });
        logger.info("当前线上用户数量:{}", sessionToUserMap.size());
    }

    /**
     * 关闭连接时触发
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String name = sessionToUserMap.remove(session);
        usersToSessionMap.remove(name);
        logger.info("断开用户{}与服务器的链接", name);
        logger.debug("剩余在线用户{}", usersToSessionMap.size());

        if (lobbyCreated.get()) {
            if (session.equals(hostSocket)) {
                logger.info("大厅房主已经退出链接，正在关闭大厅。");
                lobbyCreated.set(false);
            }
        }
    }

    CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

    /**
     * 处理ws 消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        String payload = message.getPayload();
        // 收到消息，自定义处理机制，实现业务
        logger.debug("服务器收到消息：{}", payload);
        Message m = objectMapper.readValue(payload, Message.class);
        // 若该消息是效果发动消息

        if (m instanceof TestMessage) {
            asyncThreadPoolTaskExecutor.submit(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                logger.info("done!");
            });
            return;
        }
        // 游戏进行中
        if (isPlaying.get()) {
            // 获取当前session对应的玩家
            Player p = webSocketSessionPlayerMap.get(session);
            if (m instanceof ClientActiveMessage) {
                ClientActiveMessage apply = (ClientActiveMessage) m;
                ServerAnswerActiveMessage r = new ServerAnswerActiveMessage();
                Long id = apply.getMessageId();
                r.setMessageId(id);
                // 设置回复对象
                r.setReplyId(m.getMessageId());
                r.setReply(handleClientActiveMessage(session, game, apply));
                sendMessage(session, r);
            }
            // 抽牌阶段消息，用来完成抽卡阶段
            else if (m instanceof DrawMessage) {
                game.drawInDrawPhase(p);
            } else if (m instanceof ReviveMessage) {
                ReviveMessage reviveMessage = (ReviveMessage) m;
                game.revive(p, reviveMessage.getTargetNum(), reviveMessage.getCostCardNum());
            }else if (m instanceof ClientChosenCardMessage){
                int[] cardIds = ((ClientChosenCardMessage) m).getCardIds();
                long messageId = ((ClientChosenCardMessage) m).getReplyId();
                Listener<int[]> listener = (Listener<int[]>)listenerHookMap.get(messageId);
                listener.setBlocking(cardIds);
            }
        } else {
            // 开始游戏，由房主发出，没有异步的必要。
            if (m instanceof StartGameMessage) {
                if (session == hostSocket) {
                    // check 准备状态
                    int readyPlayerNumber = readySessionSet.size();
                    // 测试阶段最小玩家人数为2;
                    if (readyPlayerNumber >= 2 && readyPlayerNumber == webSocketSessionPlayerMap.size()) {
                        this.game = GameFactory.getGameInstance(sessionToUserMap, this);
                        this.isPlaying.set(true);
                        this.webSocketSessionPlayerMap = game.webSocketSessionPlayerMap;
                    }
                }
            } else if (m instanceof ClientReadyMessage) {
                // 动作非阻塞，可以考虑用异步来提升性能。
                readySessionSet.add(session);
            }
        }
    }

    private Game game;

    private Map<WebSocketSession, Player> webSocketSessionPlayerMap;

    boolean handleClientActiveMessage(WebSocketSession webSocketSession, Game game, ClientActiveMessage message) {
        // todo 应该分离发动对象和卡片拥有者，或者增加一个字段判断是不是发动共有卡片
        Player p = webSocketSessionPlayerMap.get(webSocketSession);
        Card card = p.getHandCards().get(message.getHandCardNumber());
        return game.active(p, card, message.getEffectNumber(), message.getTargetPlayerNumber());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        exception.printStackTrace();
        logger.error("传输出现异常，关闭websocket连接:");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给个别用户之外的其余用户发送消息
     *
     * @param exception 例外
     * @param message   消息
     */
    public void sendMessageToUserExcept(Collection<WebSocketSession> exception, Message message) {
        try {
            for (WebSocketSession session : webSocketSessionPlayerMap.keySet()) {
                if (!exception.contains(session)) {
                    if (session.isOpen()) {
                        sendMessage(session, message);
                    } else {
                        throw new IllegalStateException("Target session is closed.");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 给个别用户发送消息, 用于发送隐私内容，如私下交换卡名。
     *
     * @param sessions 选项
     * @param message   消息
     */
    public void sendMessageToCertainUser(Collection<WebSocketSession> sessions, Message message) {
        try {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    sendMessage(session, message);
                } else {
                    throw new IllegalStateException("Target session is closed.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 向特定的socket发送消息
     *
     * @param session
     * @param selfDefineMessage
     * @throws IOException
     */
    public void sendMessage(WebSocketSession session, Message selfDefineMessage) throws IOException {
        session.sendMessage(new TextMessage(this.objectMapper.writeValueAsString(selfDefineMessage)));
    }

    /**
     * 向所有用户发送更新消息
     *
     * @param message
     */
    public void sendMessageToAllUsers(Message message) {
        for (Map.Entry<String, WebSocketSession> e : usersToSessionMap.entrySet()) {
            try {
                WebSocketSession webSocketSession = e.getValue();
                if (webSocketSession.isOpen()) {
                    sendMessage(webSocketSession, message);
                } else {
                    logger.warn("玩家{}客户端已离线.", e.getKey());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private final AtomicLong messageNum = new AtomicLong(0);

    /**
     * 获取消息的版本号，如果到达long极限值，则清空至0L
     * @return
     */
    private long getMessageNum() {
        return messageNum.getAndUpdate(o -> o == Long.MAX_VALUE ? 0 : o + 1);
    }

    public <T> Listener<T> sendAndWait(WebSocketSession session, Message message) throws IOException {
        long messageId = getMessageNum();
        message.setMessageId(messageId);
        this.sendMessage(session, message);
        Listener<T> listener = new Listener<>();
        listenerHookMap.put(messageId, listener);
        return listener;
    }

}
