package cn.sennri.inception.event;

public class DiscardEvent extends AbstractEvent implements Event{
    int[] cardIds;
    int subject;

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
