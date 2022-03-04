package cn.sennri.inception.client.controller;

import cn.sennri.inception.message.ClientActiveMessage;
import cn.sennri.inception.message.Message;
import cn.sennri.inception.message.ServerAnswerActiveMessage;
import cn.sennri.inception.model.listener.Listener;
import cn.sennri.inception.model.vo.ResponseBodyImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DELL
 */
@Slf4j
@RequestMapping(value = "/client")
@RestController
public class ClientController {
    /**
     * 可以从配置文件里读
     */
    private final static int PORT = 1995;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    private WebSocket webSocket = null;

    private final OkHttpClient mClient = new OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .connectTimeout(3, TimeUnit.SECONDS)
            .build();

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseBodyImpl<Response> login(@RequestParam String name, @RequestParam String remoteAddr) throws InterruptedException {
        final String url = "ws://" + remoteAddr + ":" + PORT + "/socket/server";
        // 构建一个连接请求对象
        Request request = new Request.Builder().get().url(url).header("WEB_SOCKET_USERID", name).build();
        final Listener<Response> responseListener = new Listener<>();
        this.webSocket = mClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                // 清理当前的链接池  如果断线重连会重新发request吗？
                map.clear();
                responseListener.setBlocking(response);
                log.debug("连接到服务器{}成功。", remoteAddr);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                Message m = null;
                try {
                    m = jacksonObjectMapper.readValue(text, Message.class);
                } catch (JsonProcessingException e) {
                    log.error("消息:{}反序列化失败", text);
                    e.printStackTrace();
                    // todo 如果这里是阻塞消息，是不时应该在这个地方通知对方失败了，防止接着阻塞？
                }
                if (m instanceof ServerAnswerActiveMessage) {
                    ServerAnswerActiveMessage answer = (ServerAnswerActiveMessage) m;
                    Long id = answer.getMessageId();
                    @SuppressWarnings("unchecked")
                    Listener<Boolean> booleanListener = (Listener<Boolean>) map.remove(id);
                    // todo 这里可不可能存在应答回来时， 消息已经消费完的可能？
                    // 可能 前一条消息应答过长，重连了一次
                    if (booleanListener!=null){
                        booleanListener.setBlocking(true);
                    }else{
                        log.debug("Message id{} is consumed already", id);
                    }
                }
                log.debug("Client received message:{}", text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                log.debug("已成功断开和服务器{}的链接。", remoteAddr);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
                super.onFailure(webSocket, throwable, response);
                responseListener.setBlocking(response);
                log.error("连接到服务器{}意外地失败了。", remoteAddr);
            }
        });
        Response res = responseListener.getBlocking();
        if (HttpStatus.SWITCHING_PROTOCOLS.value() == res.code()){
            return ResponseBodyImpl.createNewResponse(res, HttpStatus.OK, "连接成功，已注册到服务器中");
        }else{
            return ResponseBodyImpl.createNewResponse(res, HttpStatus.CONFLICT);
        }
        // 这里应该阻塞地等待当前open是否成功。
    }

    @ResponseBody
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseBodyImpl<String> logout() {
        if (this.webSocket == null) {
            throw new IllegalStateException("不合法的关闭");
        } else {
            webSocket.close(1000, "用户选择退出服务。");
            return ResponseBodyImpl.createNewResponse(HttpStatus.ACCEPTED);
        }
    }

    /**
     * 维护一个等待结果的消息map，根据MessageId取消息。
     */
    Map<Long, Listener<?>> map = new ConcurrentHashMap<>();

    AtomicLong messageNum = new AtomicLong(0);

    /**
     * 获取消息的版本号，如果到达long极限值，则清空至0L
     * @return
     */
    private long getMessageNum(){
        return messageNum.getAndUpdate(o -> o == Long.MAX_VALUE ? 0 : o + 1);
    }

    @ResponseBody
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public ResponseBodyImpl<?> active() throws JsonProcessingException {
        if (this.webSocket == null) {
            throw new IllegalStateException("未连接到服务器，请检查是否已登录");
        } else {
            long messageId = getMessageNum();
            ClientActiveMessage m = new ClientActiveMessage();
            m.setMessageId(messageId);
            this.webSocket.send(jacksonObjectMapper.writeValueAsString(m));
            Listener<Boolean> booleanListener = new Listener<>();
            map.put(messageId, booleanListener);
            boolean answer;
            try {
                answer = booleanListener.getBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return ResponseBodyImpl.createNewResponse(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.debug("Active {}!", answer);
            if (answer) {
                return ResponseBodyImpl.createNewResponse(HttpStatus.OK);
            } else {
                return ResponseBodyImpl.createNewResponse(HttpStatus.CONFLICT);
            }
        }
    }

}
