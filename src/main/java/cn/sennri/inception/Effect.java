package cn.sennri.inception;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.List;

/**
 * 效果接口
 * @author Sennri
 */
public interface Effect {
    /**
     * 效果是否可以发动
     * @param game
     * @return
     */
    boolean isActivable(Game game);

    /**
     * 设置效果对象
     * @param targets
     */
    void setTargets(Player[] targets);

    /**
     * 响应对方的动作，这时候需要指定对象、
     * 可能会被重写。例如，有些卡的效果发动时是在墓地的
     * @param game
     * @param targets
     */
    default void active(Game game, Player[] targets) {
        if (isActivable(game)){
            List<Effect> effectStack = game.getEffectChain();
            effectStack.add(this);
            this.setTargets(targets);
        }
    }


    /**
     * 获取效果来源卡片
     * @return
     */
    Card getEffectSource();

    /**
     * 卡牌发动者
     * @return
     */
    void setSourcePlayer(Player player);

    /**
     * 卡帕发动者，通常是卡片效果来源
     * @return
     */
    Player getSourcePlayer();

    /**
     * 执行启动效果
     * 这里写效果的处理逻辑
     * @param game 影响对象
     */
    void takeEffect(Game game);

    /**
     * 是否被无效
     * 用来判断该效果是否被终止了效果结算
     * @return
     */
    boolean isDeactivated();
}
