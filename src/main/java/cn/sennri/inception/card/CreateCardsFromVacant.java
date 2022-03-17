package cn.sennri.inception.card;

import cn.sennri.inception.AbcEffect;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.Collections;
import java.util.List;

/**
 * 凭空造物
 */
public class CreateCardsFromVacant extends AbcCard{
    public CreateCardsFromVacant(){
        this.cardName = "凭空造物";
        super.effects = Collections.singletonList(new DrawEffect(this));
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
     *
     */
    public final static class DrawEffect extends AbcEffect {
        public DrawEffect(Card effectSource) {
            super(effectSource);
        }

        /**
         * 正式启动效果
         */
        @Override
        public void takeEffect(Game game) {
            Card c = this.getEffectSource();
            Player source = c.getOwner();
            Deck deck = game.getDeck();
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
