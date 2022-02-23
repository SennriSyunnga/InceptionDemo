package cn.sennri.inception.field;

import cn.sennri.inception.card.Card;

import java.util.List;


public abstract class AbcDeck{
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

    public Card remove() {
        return deck.remove(topIndexPointer);
    }

}
