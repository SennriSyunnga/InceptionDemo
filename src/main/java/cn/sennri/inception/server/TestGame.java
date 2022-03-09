package cn.sennri.inception.server;

import cn.sennri.inception.field.TestDeckImpl;
import org.springframework.web.socket.WebSocketSession;
import java.util.Map;

public class TestGame extends Game{
    public TestGame(Map<WebSocketSession, String> sessionToUserMap) {
        super(sessionToUserMap);
        this.deck = new TestDeckImpl();
    }
}
