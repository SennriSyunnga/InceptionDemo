package cn.sennri.inception.server;

import cn.sennri.inception.config.socket.SpringWebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

/**
 * 产生game
 */
public class GameFactory {

    public static Game getGameInstance(final Map<WebSocketSession, String> sessionToUserMap, SpringWebSocketHandler handler) {
        int playerNum = sessionToUserMap.size();
        if (playerNum == 2) {
            return new TestGame(sessionToUserMap, handler);
        } else {
            return new Game(sessionToUserMap, handler);
        }
    }

}
