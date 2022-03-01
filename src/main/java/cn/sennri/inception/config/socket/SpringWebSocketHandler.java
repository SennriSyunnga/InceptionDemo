package cn.sennri.inception.config.socket;

import cn.sennri.inception.message.ClientActiveMessage;
import cn.sennri.inception.message.Message;
import cn.sennri.inception.message.ServerAnswerActiveMessage;
import cn.sennri.inception.model.listener.Listener;
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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
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
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
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
        logger.debug("关闭web socket连接");
        logger.debug("剩余在线用户{}", usersToSessionMap.size());
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
        // 这里实现消息的反序列化处理

        Message m = objectMapper.readValue(payload , Message.class);
        if (m instanceof ClientActiveMessage){
            ClientActiveMessage apply = (ClientActiveMessage) m;
            Long id = apply.getMessageId();
            ServerAnswerActiveMessage r = new ServerAnswerActiveMessage();
            r.setMessageId(id);
            r.setReply(true);
            String s = objectMapper.writeValueAsString(r);
            session.sendMessage(new TextMessage(s));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        exception.printStackTrace();
        logger.debug("传输出现异常，关闭websocket连接:");
        //    String userId= (String) session.getAttributes().get(USER_ID);
        //    users.remove(userId);
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
