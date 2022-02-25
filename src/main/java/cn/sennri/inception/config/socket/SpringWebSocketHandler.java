package cn.sennri.inception.config.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpringWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(SpringWebSocketHandler.class);

    /**
     * Map来存储WebSocketSession，key用USER_ID 即在线用户列表
     */
    private static final Map<String, WebSocketSession> users;

    /**
     * 用户自定义标识 对应监听器从的key
     */
    private static final String USER_ID = "WEB_SOCKET_USERID";


    static {
        users =  new HashMap<String, WebSocketSession>();
    }

    public SpringWebSocketHandler() {}

    /**
     * 连接成功时候，会触发页面上onopen方法
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        System.out.println("成功建立websocket连接!");
        String userId = (String) session.getAttributes().get(USER_ID);
        users.put(userId,session);
        System.out.println("当前线上用户数量:"+ users.size());

        //这块会实现自己业务，比如，当用户登录后，会把离线消息推送给用户
        //TextMessage returnMessage = new TextMessage("成功建立socket连接，你将收到的离线");
        //session.sendMessage(returnMessage);
    }

    /**
     * 关闭连接时触发
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.debug("关闭websocket连接");
        String userId = (String) session.getAttributes().get(USER_ID);
        System.out.println("用户"+userId+"已退出！");
        users.remove(userId);
        System.out.println("剩余在线用户"+users.size());
    }

    /**
     * js调用websocket.send时候，会调用该方法
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        // 收到消息，自定义处理机制，实现业务
        System.out.println("服务器收到消息："+message);
        // 这里实现消息的反序列化处理
        if(message.getPayload().startsWith("#anyone#")){
            sendMessageToUser((String) session.getAttributes().get(USER_ID),
                    new TextMessage("服务器单发：" +message.getPayload()));
        }else if(message.getPayload().startsWith("#everyone#")){
            sendMessageToUsers(new TextMessage("服务器群发：" +message.getPayload()));
        }else{

        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(session.isOpen()){
            session.close();
        }
        logger.debug("传输出现异常，关闭websocket连接... ");
        String userId= (String) session.getAttributes().get(USER_ID);
        users.remove(userId);
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
        for (String id : users.keySet()) {
            if (id.equals(userId)) {
                try {
                    if (users.get(id).isOpen()) {
                        users.get(id).sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public void sendMessageToUsers(TextMessage message) {
        for (String userId : users.keySet()) {
            try {
                if (users.get(userId).isOpen()) {
                    users.get(userId).sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
