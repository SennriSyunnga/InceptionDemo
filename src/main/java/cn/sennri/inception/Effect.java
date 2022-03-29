package cn.sennri.inception;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;
import cn.sennri.inception.util.GameUtils;

/**
 * 效果接口
 * @author Sennri
 */
public interface Effect {
    /**
     * 设置效果对象 不能确定对象是玩家或者卡片
     * @param targets
     */
    void setTargets(int[] targets);

    /**
     * 在客户端角度看效果是否可以发动
     * @param game
     * @return
     */
    boolean isActivable(Game game);

    /**
     * 用来统计发动次数
     */
    void active(Player player, int[] targets);

    /**
     * 服务端层面的校验, 检查对象是否合法。
     * @param game
     * @param source
     * @param targets
     * @return
     */
    default boolean isActivationLegal(Game game, Player source, int[] targets){
        return isActivable(game);
    }

    /**
     * 卡片文字描述信息。
     * @return
     */
    String getDescription();

    /**
     * 获取效果来源卡片
     * @return
     */
    Card getEffectSource();

    /**
     * 设置效果发动源
     * @param player 卡牌发动者
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

    /**
     * 无效化
     */
    void setDeactivated();

    /**
     * 默认效果没有对象
     * @return 效果对象类型，结合targets来正确处理数组内容
     */
    default GameUtils.TargetTypeEnum getTargetType(){return GameUtils.TargetTypeEnum.NONE;}
}
