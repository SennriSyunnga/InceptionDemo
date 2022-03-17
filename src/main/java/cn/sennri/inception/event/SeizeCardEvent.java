package cn.sennri.inception.event;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class SeizeCardEvent extends AbstractEvent{
    int toPlayerId;
    int fromPlayerId;
    int[] cardNum;
    public int getToPlayerId() {
        return toPlayerId;
    }

    public int getFromPlayerId() {
        return fromPlayerId;
    }

    public int[] getCardNum() {
        return cardNum;
    }
}
