package cn.sennri.inception.card;


import cn.sennri.inception.Effect;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.List;

/**
 * 应该为卡抽象卡的效果类吗？
 */
public interface Card {
    /**
     * 获取效果列表
     * @return
     */
    List<Effect> getEffects();

    /**
     * 获取效果列表
     * @return
     */
    default Effect getEffect(int num) {
        return getEffects().get(num);
    }

    /**
     * 获取卡的所属，若未抽取，则卡片归属为null
     * @return
     */
    Player getOwner();

    /**
     * 抽取卡或者交换控制权时Owner改变
     * @return 旧拥有者
     */
    Player setOwner(Player newOwner);

    /**
     * 每张卡应该有一个效果工厂，用于返回效果
     * 同时每一张卡应该为不同效果置一个计数器，判断是否还可以发动
     * @param num
     * @param targets 复数个效果对象
     * @return
     */
    Effect activeEffect(int num, Player[] targets);

    default boolean isActivable(Game game){
        for (Effect e:this.getEffects()){
            if (e.isActivable(game)){
                return true;
            }
        }
        return false;
    }
}
