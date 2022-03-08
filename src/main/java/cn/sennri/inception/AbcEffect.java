package cn.sennri.inception;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

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
    protected Card effectSource;
    /**
     * 卡片指定的效果对象
     */
    protected Player[] targetPlayers;

    /**
     * 卡片指定的效果对象
     */
    protected int[] targets;
    /**
     * 是否被康
     */
    protected boolean deactivated = false;

    protected int maxCount;

    /**
     * 回合已发动次数
     */
    protected int activeCount;

    @Override
    public Card getEffectSource() {
        return effectSource;
    }

    @Override
    public boolean isDeactivated() {
        return deactivated;
    }

    @Override
    public void setTargets(int[] targets) {
        this.targets = targets;
    }

    //void setTargets(Player[] targets);

    public AbcEffect(Card effectSource) {
        this.effectSource = effectSource;
    }

    public void refresh() {
        this.activeCount = 0;
        this.deactivated = false;
    }

    protected Player source;

    /**
     * 默认实现
     * @param game
     * @return
     */
    @Override
    public boolean isActivable(Game game) {
        if (this.getEffectSource().getOwner() != game.getTurnOwner()) {
            return false;
        }
        if (game.getPhase() != Game.Phase.USE_PHASE) {
            return false;
        }
        return true;
    }

    /**
     * 设置发动来源
     * @return
     */
    @Override
    public void setSourcePlayer(Player player) {
        this.source = player;
    }

    /**
     * 默认返回卡牌持有者
     * @return
     */
    @Override
    public Player getSourcePlayer() {
        return getEffectSource().getOwner();
    }


    @Override
    public void setDeactivated() {
        this.deactivated = true;
    }
}
