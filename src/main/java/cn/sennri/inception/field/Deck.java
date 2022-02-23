package cn.sennri.inception.field;

import cn.sennri.inception.card.Card;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface Deck {
    /**
     * 抽卡
     * @return
     */
    public Card draw();


    public Card remove();

    /**
     * 卡组顶弃牌
     * @param graveyard
     * @param tempGraveyard
     */
    void abandon(List<Card> graveyard, List<Card> tempGraveyard);
    public void shuffle();
    public boolean isEmpty();
}
