package cn.sennri.inception.field;

import cn.sennri.inception.card.*;

import java.util.ArrayList;

public class TestDeckImpl extends AbcDeck{
    public TestDeckImpl(){
        deck = new ArrayList<>();
        int cardUid = 0;
        for(int i = 0;i < 15;i++){
            Card card = new Decrypt();
            deck.add(card);
            card.setUid(cardUid);
            cardUid++;
        }
        for(int i = 0;i < 15;i++){
            Card card = new Shoot();
            deck.add(card);
            card.setUid(cardUid);
            cardUid++;
        }
        for(int i = 0;i < 15;i++){
            Card card = new DreamShuttle();
            deck.add(card);
            card.setUid(cardUid);
            cardUid++;
        }
        for(int i = 0;i < 2;i++){
            Card card = new TimeStorm();
            deck.add(card);
            card.setUid(cardUid);
            cardUid++;
        }
        for(int i = 0;i < 5;i++){
            Card card = new CreateCardsFromVacant();
            deck.add(card);
            card.setUid(cardUid);
            cardUid++;
        }
        uidToCard = deck.toArray(new Card[0]);
        topIndexPointer = deck.size() - 1;
    }
}
