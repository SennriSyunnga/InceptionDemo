package cn.sennri.inception.player;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.util.Utils;

import java.util.List;

/**
 * 角色卡
 * draw行为应该下发到下面这个Role来代为实现。
 */
public interface Role {
    /**
     * 产生roll点结果
     * @return
     */
    default int roll(){
        return Utils.roll();
    }

    /**
     * 产生Shoot卡片roll结果
     * @return
     */
    default int rollShootResult(){
        return roll();
    }

    /**
     * 复活他人
     * @param p
     */
    default void revive(Player p){
        p.revive();
    }

    default void draw(List<Card> hands, Deck deck){
        hands.add(deck.draw());
    }
}
