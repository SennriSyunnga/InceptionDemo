package cn.sennri.inception.client.controller;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.ByteString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.concurrent.TimeUnit;

/**
 * @author DELL
 */
@Slf4j
@RequestMapping(value = "/client")
public class ViewController {
    /**
     * 可以从配置文件里读
     */
    private final static int PORT = 1995;

    private WebSocket webSocket = null;
    private final OkHttpClient mClient = new OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .connectTimeout(3, TimeUnit.SECONDS)
            .build();

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void login(String name, String targetAddress){
        final String url = "ws://"+ targetAddress +":"+ PORT +"/socket/server";
        // 构建一个连接请求对象
        Request request = new Request.Builder().get().url(url).header("WEB_SOCKET_USERID", name).build();
        this.webSocket = mClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                log.debug("连接到服务器{}成功。", targetAddress);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                if ("1".equals(text)){
                    webSocket.send("2");
                }
                log.debug("Client receives message:{}", text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                log.debug("已断开和服务器{}的链接。", targetAddress);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
                super.onFailure(webSocket, throwable, response);
                log.error("连接到服务器{}失败。", targetAddress);
            }
        });
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public cn.sennri.inception.model.vo.Response<String> logout(){
        if (this.webSocket == null){
            throw new IllegalStateException("不合法的关闭");
        }else{
            webSocket.close(1000, "用户选择退出服务。");
            return cn.sennri.inception.model.vo.Response.createNewResponse(HttpStatus.ACCEPTED);
        }
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public cn.sennri.inception.model.vo.Response<String> active(){
        if (this.webSocket == null){
            throw new IllegalStateException("不合法的关闭");
        }else{
            
        }
    }

}
