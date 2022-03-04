package cn.sennri.inception.config.socket;

import cn.sennri.inception.Effect;
import cn.sennri.inception.card.Card;
import cn.sennri.inception.message.*;
import cn.sennri.inception.model.listener.Listener;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


public class SpringWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    ObjectMapper objectMapper;


    private static final Logger logger = LoggerFactory.getLogger(SpringWebSocketHandler.class);

    /**
     * Map来存储WebSocketSession，key用USER_ID 即在线用户列表
     */
    private static final Map<String, WebSocketSession> usersToSessionMap;

    private static final Map<WebSocketSession, String> sessionToUserMap;

    /**
     * 用户自定义标识 对应监听器从的key
     */
    private static final String USER_ID = "WEB_SOCKET_USERID";

    /**
     * 维护一个等待结果的消息map，根据MessageId取消息。
     */
    Map<Long, Listener<?>> map = new ConcurrentHashMap<>();


    static {
        usersToSessionMap = new ConcurrentHashMap<>();
        sessionToUserMap = new ConcurrentHashMap<>();
    }

    public SpringWebSocketHandler() {
    }

    /**
     * 连接成功时候，会触发页面上onopen方法
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        InetAddress inetAddress = Optional.ofNullable(session.getRemoteAddress()).orElseThrow(NullPointerException::new).getAddress();
        logger.debug("WebSocket成功建立与{}的连接!", inetAddress);
        // 若非空，则携带信息，当前为测试，因此不强制要求携带信息；
        Optional.ofNullable(session.getHandshakeHeaders().get(USER_ID)).ifPresent(o -> {
            String name = o.get(0);
            logger.debug("会话{}将自身标识为{}, 将使用该身份作为唯一标识",inetAddress, name);
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

        Optional.ofNullable(session.getHandshakeHeaders().get(USER_ID)).ifPresent(o -> {
            String name = sessionToUserMap.remove(session);
            usersToSessionMap.remove(name);
            logger.info("断开用户{}与服务器的链接", name);
        });
        logger.debug("关闭web socket连接;\n" +
                "剩余在线用户{}",usersToSessionMap.size());
    }

    /**
     * js调用websocket.send时候，会调用该方法
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        String payload = message.getPayload();
        // 收到消息，自定义处理机制，实现业务
        logger.debug("服务器收到消息：{}", payload);
        Message m = objectMapper.readValue(payload , Message.class);
        // 若该消息是效果发动消息
        Player p = webSocketSessionPlayerMap.get(session);
        if (m instanceof ClientActiveMessage){
            ClientActiveMessage apply = (ClientActiveMessage) m;
            ServerAnswerActiveMessage r = new ServerAnswerActiveMessage();
            Long id = apply.getMessageId();
            r.setMessageId(id);
            // 设置回复对象
            r.setReplyId(m.getMessageId());
            r.setReply(handleClientActiveMessage(session, game, apply));
            String s = objectMapper.writeValueAsString(r);
            session.sendMessage(new TextMessage(s));
        }else if (m instanceof DrawMessage){
            if (game.getPhase().equals(Game.Phase.DRAW_PHASE)){
                game.draw(p);
                // 开始应答
            }
        }
    }

    Game game;

    Map<WebSocketSession, Player> webSocketSessionPlayerMap = new ConcurrentHashMap<>();


    boolean handleClientActiveMessage(WebSocketSession webSocketSession, Game game, ClientActiveMessage message){
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
        logger.debug("传输出现异常，关闭websocket连接:");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给某个用户发送消息
     *
     * @param userId
     * @param message
     */
    public void sendMessageToUser(String userId, TextMessage message) {
        WebSocketSession session = usersToSessionMap.get(userId);
        try {
            if (session.isOpen()) {
                session.sendMessage(message);
            }else{
                throw new IllegalStateException("Target session is closed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public void sendMessageToUsers(TextMessage message) {
        for ( Map.Entry<String, WebSocketSession> e: usersToSessionMap.entrySet()) {
            try {
                WebSocketSession webSocketSession = e.getValue();
                if (webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(message);
                }else{
                    logger.warn("{} is offline.", e.getKey());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
