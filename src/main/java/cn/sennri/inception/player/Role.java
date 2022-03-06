package cn.sennri.inception.player;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.util.Utils;

import java.util.List;

/**
 * 角色卡
 * draw行为应该下发到下面这个Role来代为实现。
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

    void awakenBy(Player player);

    /**
     * 基本的复活复活他人的接口
     * @param p
     */
    boolean revive(Player p, int[] num);

    void commonDraw(Deck deck);

    boolean discard(int[] num);

    boolean canShoot(Player other);
}
