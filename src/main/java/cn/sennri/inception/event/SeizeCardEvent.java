package cn.sennri.inception.event;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class SeizeCardEvent extends AbstractEvent{
    int num;
    int toPlayerId;
    int fromPlayerId;
    int[] cardIds;

    public int getNum() {
        return num;
    }

    public int getToPlayerId() {
        return toPlayerId;
    }

    public int getFromPlayerId() {
        return fromPlayerId;
    }

    public int[] getCardIds() {
        return cardIds;
    }
}
