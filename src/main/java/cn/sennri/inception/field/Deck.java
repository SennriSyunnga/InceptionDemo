package cn.sennri.inception.field;

import cn.sennri.inception.card.Card;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface Deck {
    // todo getType


    /**
     * 返回卡组大小
     * @return
     */
    public int size();

    /**
     * 抽卡，底层调用remove
     * @return
     */
    public Card draw();


    /**
     * 移除牌顶卡片，该方法不会失败，除非初始卡库就为空值。
     * @return
     */
    public Card remove();

    /**
     * 卡组顶弃牌
     * @param graveyard
     * @param tempGraveyard
     */
    void abandon(List<Card> graveyard, List<Card> tempGraveyard);

    /**
     * 洗牌
     */
    public void shuffle();

    /**
     * 牌库判空
     * @return
     */
    public boolean isEmpty();

    Card[] getUidToCard();
}
