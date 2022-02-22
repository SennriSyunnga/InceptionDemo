package cn.sennri.inception.player;

import java.net.InetAddress;

/**
 * @author DELL
 */
public class PlayerFactory {
    public static Player getNewPlayer(String playerName, InetAddress inetAddress, AbcPlayer.ModeEnum mode){
        if ("BasePlayer".equals(playerName)){
            return new BasePlayer(inetAddress);
        }
        return null;
    }
    public static BasePlayer getNewBasePlayer(InetAddress inetAddress){
        return new BasePlayer(inetAddress);
    }

}
