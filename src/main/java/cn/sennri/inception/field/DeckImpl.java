package cn.sennri.inception.field;

import java.util.ArrayList;


public class DeckImpl extends AbcDeck implements Deck {

    final static int MAX_CAPABILITY = 70;

    public DeckImpl(){
        deck = new ArrayList<>(MAX_CAPABILITY);
    }


}
