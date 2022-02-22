package cn.sennri.inception;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.player.Player;

/**
 * @Classname AbcEffect
 * @Description TODO
 * @Date 2022/2/7 11:07
 * @Created by Sennri
 */
public abstract class AbcEffect implements Effect{
    /**
     * 效果的来源
     */
    Card effectSource;
    /**
     * 卡片指定的效果对象
     */
    Player[] targets;
    /**
     * 是否被康
     */
    boolean deactivated;

    @Override
    public boolean isDeactivated() {
        return deactivated;
    }


    public AbcEffect(Card effectSource){

    }
}
