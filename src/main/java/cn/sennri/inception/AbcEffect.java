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
     * 效果的描述条文
     */
    public String description;

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * 效果的来源
     */
    protected Card effectSource;

    /**
     * 卡片指定的效果对象
     */
    protected int[] targets;
    /**
     * 是否被康
     */
    protected boolean deactivated = false;

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

    public AbcEffect(Card effectSource) {
        this.effectSource = effectSource;
    }

    /**
     * 重置计数信息
     */
    public void refresh() {
        this.activeCount = 0;
        this.deactivated = false;
    }

    protected Player source;

    /**
     * 默认实现，验证卡牌持有者是否为回合玩家，且到了出牌阶段
     * 大部分主动效果都需要在本回合的出牌阶段才能发动，因此采用这个校验作为默认实现。
     * @param game
     * @return
     */
    @Override
    public boolean isActivable(Game game) {
        // 校验是否为卡牌拥有者;如果为场地效果需要复写这整个方法
        if (this.getEffectSource().getOwner() != game.getTurnOwner()) {
            return false;
        }
        // 如果在asking阶段，应该拒绝效果的发动。
        if(game.getIsAsking().get()){
            return false;
        }
        // 当前是否在出牌阶段
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


    /**
     * 用来统计发动次数
     */
    @Override
    public void active(Player player, int[] targets) {
        setSourcePlayer(player);
        setTargets(targets);
    }
}
