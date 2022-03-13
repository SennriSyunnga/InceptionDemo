package cn.sennri.inception.event;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class TurnEndEvent extends AbstractEvent {
    int nextTurnPlayerId;

    public int getNextTurnPlayerId() {
        return nextTurnPlayerId;
    }
}
