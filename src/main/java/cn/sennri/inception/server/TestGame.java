package cn.sennri.inception.server;

import cn.sennri.inception.config.socket.SpringWebSocketHandler;
import cn.sennri.inception.field.TestDeckImpl;
import org.springframework.web.socket.WebSocketSession;
import java.util.Map;

public class TestGame extends Game{
    public TestGame(Map<WebSocketSession, String> sessionToUserMap, SpringWebSocketHandler handler) {
        super(sessionToUserMap, handler);
        this.deck = new TestDeckImpl();
    }
}
