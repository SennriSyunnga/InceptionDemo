package cn.sennri.inception.server;

import org.springframework.web.socket.WebSocketSession;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 产生game
 */
public class GameFactory {
    public static Game getGameInstance(final Map<WebSocketSession, String> sessionToUserMap){
        int playerNum = sessionToUserMap.size();
        List<InetAddress> addressList = sessionToUserMap.keySet().stream().map(o -> o.getRemoteAddress().getAddress()).collect(Collectors.toList());
        if (playerNum == 2){
            return new TestGame(addressList);
        }else{
            return new Game(addressList);
        }
    }
}
