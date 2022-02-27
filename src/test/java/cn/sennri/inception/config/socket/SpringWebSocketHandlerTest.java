package cn.sennri.inception.config.socket;


import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.ByteString;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Slf4j
public class SpringWebSocketHandlerTest {
    private static final Logger logger = LoggerFactory.getLogger(SpringWebSocketHandler.class);

    @Test
    public void testSocketConnection(){
        OkHttpClient mClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间
                .build();
        String url = "ws://localhost:1995/socket/server";
        //构建一个连接请求对象
        Request request = new Request.Builder().get().url(url).header("WEB_SOCKET_USERID","1").build();
        WebSocket websocket = mClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                //连接成功...
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                if ("1".equals(text)){
                    webSocket.send("2");
                }
                logger.debug("client receive{}",text);
                //收到消息...（一般是这里处理json）
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                //收到消息...（一般很少这种消息）
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                //连接关闭...
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
                super.onFailure(webSocket, throwable, response);
                //连接失败...
            }
        });
        boolean status = websocket.send("0");
        return;
    }

}