package cn.sennri.inception.server;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

/**
 * 产生game
 */
public class GameFactory {
    public static Game getGameInstance(final Map<WebSocketSession, String> sessionToUserMap) {
        int playerNum = sessionToUserMap.size();
        if (playerNum == 2) {
            return new TestGame(sessionToUserMap);
        } else {
            return new Game(sessionToUserMap);
        }
    }
}
