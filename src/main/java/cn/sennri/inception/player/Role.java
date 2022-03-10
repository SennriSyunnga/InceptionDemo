package cn.sennri.inception.player;

import cn.sennri.inception.util.Utils;

/**
 * 角色卡
 * draw行为应该下发到下面这个Role来代为实现。
 * 这里应该定义一些永续效果，设置与记录回调等
 */
public interface Role {
    /**
     * 产生roll点结果
     * @return
     */
    default int roll(){
        return Utils.roll();
    }

    /**
     * 产生Shoot卡片roll结果
     * @return
     */
    default int rollShootResult(){
        return roll();
    }

    /**
     * 被重生，在这里触发相应记录
     * @param player
     */
    void awakenBy(Player player);

    /**
     * 基本的复活复活他人的接口
     * @param p
     */
    boolean revive(Player p, int[] num);

    /**
     * 抽牌阶段抽卡
     */
    void commonDraw();

    /**
     * 弃牌
     * @param num
     * @return
     */
    boolean discard(int[] num);

    /**
     * 判断是否可以攻击
     * @param other
     * @return
     */
    boolean canShoot(Player other);

    /**
     * 重置回合信息
     */
    void refresh();

    /**
     * 成功解封
     */
    void decrypted();

    /**
     * 是否有办法使用解锁
     * @return
     */
    boolean canDecrypt();
}
