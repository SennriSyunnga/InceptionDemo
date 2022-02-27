package cn.sennri.inception.config.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Configuration
@EnableWebSocket
public class SpringWebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(),"/socket/server")
                .addInterceptors(new SpringWebSocketHandlerInterceptor())
                .setAllowedOrigins("*");

//        registry.addHandler(webSocketHandler(), "/sockjs/server")
//                .addInterceptors(new SpringWebSocketHandlerInterceptor()).setAllowedOrigins("*").withSockJS();
    }

    @Bean
    public TextWebSocketHandler webSocketHandler(){
        return new SpringWebSocketHandler();
    }
}
