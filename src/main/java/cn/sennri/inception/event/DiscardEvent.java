package cn.sennri.inception.event;

public class DiscardEvent {
    int[] cardIds;
    int subject;

    public int getSubject() {
        return subject;
    }

    public int[] getCardIds() {
        return cardIds;
    }
}
