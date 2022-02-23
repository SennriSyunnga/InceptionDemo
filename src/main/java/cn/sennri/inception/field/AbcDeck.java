package cn.sennri.inception.field;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.server.Listener;

import java.util.List;


public abstract class AbcDeck{

    List<Listener> listeners;

    /**
     * 没有具体实现，请在实现类里头进行赋值
     */
    public List<Card> deck;

    /**
     * 指向卡顶
     */
    public int topIndexPointer;

    /**
     * 效果来源，通过{@link Card#getOwner()}可以获取到效果的发动者。
     */
    public Card effectSource;

    public Card draw() {
        // 考虑在这里放个listener。
        return remove();
    }

     void abandon(List<Card> graveyard, List<Card> tempGraveyard){
        Card c = remove();
        tempGraveyard.add(c);
        graveyard.add(c);
    }

    void notifyDeckListeners(){
        for (Listener l:listeners){
            l.onHostSuccess();
        }
    }

    public Card remove() {
        Card c = deck.remove(topIndexPointer);
        if (topIndexPointer == 0){
            notifyDeckListeners();
        }
        topIndexPointer--;
        return c;
    }

}
