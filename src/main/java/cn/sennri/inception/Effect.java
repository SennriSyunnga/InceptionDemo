package cn.sennri.inception;


import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.player.Player;

import java.util.ArrayDeque;
import java.util.List;

/**
 * @Classname Effect
 * @Description TODO
 * @Date 2022/2/6 22:40
 * @Created by Sennri
 */
public interface Effect {
    /**
     * 通过effectStack确定是否前面发动的卡可以被你响应
     * 是否存在骰子事件
     * 是否存在
     * 墓地里是否有需要的卡
     * 是否有骰子结果
     * @param effectStack
     * @return
     */
    public boolean isActivable(ArrayDeque<Effect> effectStack);

    /**
     * 响应对方的动作，这时候需要指定对象、
     * @param effectStack
     */
    public void active(ArrayDeque<Effect> effectStack);

    /**
     * 正式启动效果
     * @param effectStack
     * @param deck
     * @param graveyard
     * @param exclusionZone
     * @param source
     * @param target
     */
    public void takeEffect(ArrayDeque<Effect> effectStack, Deck deck, List<Card> graveyard, List<Card> exclusionZone, Player source, Player[] target);

    /**
     * 是否被无效
     * 用来判断该效果是否被终止了效果结算
     * @return
     */
    public boolean isDeactivated();
}
