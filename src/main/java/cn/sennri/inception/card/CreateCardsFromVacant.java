package cn.sennri.inception.card;

import cn.sennri.inception.AbcEffect;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;

/**
 * 凭空造物
 */
public class CreateCardsFromVacant extends AbcCard{

    CreateCardsFromVacant(){
        super();
        super.effects = Collections.singletonList(new Effect(this));
    }

    /**
     *
     * @return
     */
    @Override
    public List<cn.sennri.inception.Effect> getEffects() {
        return super.effects;
    }


    /**
     * 每张卡应该有一个效果工厂，用于返回效果
     * 同时每一张卡应该为不同效果置一个计数器，判断是否还可以发动
     * @param num
     * @param targets 复数个效果对象
     * @return
     */
    @Override
    public cn.sennri.inception.Effect activeEffect(int num, Player[] targets) {
        return null;
    }

    /**
     *
     */
    public final static class Effect extends AbcEffect {

        public boolean isActivable(Game game) {
            return true;
        }
        public Effect(Card effectSource) {
            super(effectSource);
        }

        @Override
        public boolean isActivable(ArrayDeque<cn.sennri.inception.Effect> effectStack) {
            return true;
        }

        /**
         * 正式启动效果
         * @param effectStack
         * @param graveyard
         * @param exclusionZone
         * @param source
         * @param target
         */
        @Override
        public void takeEffect(ArrayDeque<cn.sennri.inception.Effect> effectStack,
                               Deck deck,
                               List<Card> graveyard,
                               List<Card> exclusionZone,
                               Player source,
                               Player[] target) {
            List<Card> handCards = source.getHandCards();
            for (int i = 0;i < 2;i++){
                Card card = deck.draw();
                if (card == null){
                    // do sth;
                    return;
                }
                card.setOwner(source);
                handCards.add(card);
            }
        }
    }

}
