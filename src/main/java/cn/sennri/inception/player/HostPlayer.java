package cn.sennri.inception.player;

import cn.sennri.inception.server.Game;
import org.springframework.web.socket.WebSocketSession;

/**
 * @Classname HostPlayer
 * @Description TODO
 * @Date 2022/2/6 21:32
 * @Created by Sennri
 */
public class HostPlayer extends BasePlayer{
    public HostPlayer(Game game, WebSocketSession socketSession, String name) {
        super(game, socketSession, name);
        // 毋庸置疑
        this.order = 0;
    }

    /**
     * 使得骰子结果减少1, 该结果不通过role实现
     * @return
     */
    @Override
    public int rollShootResult() {
        return Math.max(super.rollShootResult() - 1, 1);
    }

    /**
     * 无条件复活
     * @param p
     * @param num
     * @return
     */
    @Override
    public boolean revive(Player p, int[] num) {
        if (p.equals(this)){
            this.awakenBy(p);
            return true;
        }
        return super.revive(p, num);
    }
}
