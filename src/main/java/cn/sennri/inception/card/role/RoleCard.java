package cn.sennri.inception.card.role;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.util.GameUtils;

/**
 * 角色卡
 * draw行为应该下发到下面这个Role来代为实现。
 * 这里应该定义一些永续效果，设置与记录回调等
 */
public interface RoleCard extends Card {
    /**
     * 产生roll点结果
     * @return
     */
    default int roll(){
        return GameUtils.roll();
    }

    /**
     * 产生Shoot卡片roll结果
     * @return
     */
    default int rollShootResult(){
        return roll();
    }

    /**
     * 被重生，在这里触发相应回调或者执行计数
     * @param player
     */
    void awakenBy(Player player);

    /**
     * 基本的复活复活他人的接口
     * @param p
     */
    boolean revive(Player p, int[] num);

    void draw(int times);

    /**
     * 抽牌阶段抽卡
     */
    void drawInDrawPhase();

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
