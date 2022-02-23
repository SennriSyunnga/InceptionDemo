package cn.sennri.inception;


import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.ArrayDeque;
import java.util.List;

/**
 * @Classname Effect
 * @Description TODO
 * @Date 2022/2/6 22:40
 * @Created by Sennri
 */
public interface Effect {

    boolean isActivable(Game game);

    void setTargets(Player[] targets);

    /**
     * 响应对方的动作，这时候需要指定对象、
     * @param game
     * @param targets
     */
    default void active(Game game, Player[] targets) {
        List<Effect> effectStack = game.getEffectChain();
        effectStack.add(this);
        this.setTargets(targets);
    }

    /**
     * 获取效果来源卡片
     * @return
     */
    Card getEffectSource();

    /**
     * 执行启动效果
     * 这里写效果的处理逻辑
     */
    public void takeEffect(Game game);

    /**
     * 是否被无效
     * 用来判断该效果是否被终止了效果结算
     * @return
     */
    public boolean isDeactivated();
}
