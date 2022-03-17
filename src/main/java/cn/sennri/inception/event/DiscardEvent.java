package cn.sennri.inception.event;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class DiscardEvent extends AbstractEvent implements Event{
    /**
     * 弃牌玩家的序号
     */
    int subject;

    /**
     * 在手卡中的编号
     */
    int[] cardIds;


    public void setCardIds(int[] cardIds) {
        this.cardIds = cardIds;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }

    public int getSubject() {
        return subject;
    }

    public int[] getCardIds() {
        return cardIds;
    }
}
