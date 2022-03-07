package cn.sennri.inception.player;

import cn.sennri.inception.server.Game;

import java.net.InetAddress;

/**
 * @Classname HostPlayer
 * @Description TODO
 * @Date 2022/2/6 21:32
 * @Created by Sennri
 */
public class HostPlayer extends BasePlayer{
    public HostPlayer(Game game, InetAddress inetAddress) {
        super(game, inetAddress);
    }

    public HostPlayer(InetAddress inetAddress) {
        super(inetAddress);
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
