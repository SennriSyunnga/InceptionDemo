package cn.sennri.inception.field;

import cn.sennri.inception.card.Card;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface Deck {
    public Card draw();

    /**
     * 由于展示多于从上端、下端操作。
     * @param num
     * @param graveyard
     * @param exclusionZone
     */
    public void abandon(int num, List<Card> graveyard, List<Card> exclusionZone);
    public void shuffle();
    public boolean isEmpty();
    CountDownLatch getHostWinCondition();
}
