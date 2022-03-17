package cn.sennri.inception.card;

import cn.sennri.inception.Effect;
import cn.sennri.inception.client.view.FieldView;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.List;

/**
 * 是否应该同时为RoleCard的抽象？
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

    String getCardName();

    void setUid(int uid);

    int getUid();

    /**
     * 获取卡的所属，若未抽取，则卡片归属为null
     * @return
     */
    Player getOwner();

    /**
     * 抽取卡或者交换控制权时Owner改变
     */
    void setOwner(Player newOwner);

    /**
     * 仅仅是客户端级别的校验
     * 但客户端没有game啊。
     * @param game
     * @return
     */
    default boolean isActivable(Game game){
        for (Effect e:this.getEffects()){
            if (e.isActivable(game)){
                return true;
            }
        }
        return false;
    }

    // todo
    default boolean isActivable(FieldView view){
        return false;
    }

}
