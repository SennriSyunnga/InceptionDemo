package cn.sennri.inception.field;

import cn.sennri.inception.card.Card;

import java.util.ArrayList;
import java.util.List;


public class DeckImpl extends AbcDeck implements Deck {



    final static int MAX_CAPABILITY = 70;

    public DeckImpl(){
        deck = new ArrayList<>(MAX_CAPABILITY);
    }


    /**
     * 效果逻辑不该在deck里完成。
     * @param graveyard
     * @param tempGraveyard
     */
    @Override
    public void abandon(List<Card> graveyard, List<Card> tempGraveyard) {

    }

    @Override
    public void shuffle() {

    }

    @Override
    public boolean isEmpty() {
        return this.deck.isEmpty();
    }


}
