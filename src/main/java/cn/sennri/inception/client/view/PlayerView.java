package cn.sennri.inception.client.view;

import cn.sennri.inception.player.Player;

/**
 * client端可以看见的内容信息
 */
public class PlayerView {
    /**
     * 手卡剩余量
     */
    int handCardNum;

    /**
     * 位置
     */
    Player.PositionEnum position;

    /**
     *
     */
    Player.ModeEnum mode;

    /**
     * 是否迷失
     */
    Player.StatusEnum status;


}
