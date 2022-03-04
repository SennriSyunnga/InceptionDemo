package cn.sennri.inception;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.player.Player;

/**
 * @Classname AbcEffect
 * @Description TODO
 * @Date 2022/2/7 11:07
 * @Created by Sennri
 */
public abstract class AbcEffect implements Effect {
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

    int maxCount;

    /**
     * 回合已发动次数
     */
    int activeCount;

    @Override
    public Card getEffectSource() {
        return effectSource;
    }

    @Override
    public boolean isDeactivated() {
        return deactivated;
    }

    @Override
    public void setTargets(Player[] targets) {
        this.targets = targets;
    }

    protected AbcEffect(Card effectSource) {
        this.effectSource = effectSource;
    }

    public void refresh() {
        this.activeCount = 0;
    }

    Player source;

    /**
     * 设置发动来源
     * @return
     */
    @Override
    public void setSourcePlayer(Player player){
        this.source = player;
    }

    /**
     * 默认返回卡牌持有者
     * @return
     */
    @Override
    public Player getSourcePlayer(){
        return getEffectSource().getOwner();
    }
}
