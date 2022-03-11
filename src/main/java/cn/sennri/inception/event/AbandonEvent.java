package cn.sennri.inception.event;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class AbandonEvent extends AbstractEvent{
    private int[] cardIds;

    public int[] getCardIds() {
        return cardIds;
    }
}
