package cn.sennri.inception.event;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RollEvent extends AbstractEvent{
    int playerId;
    int result;

    public int getPlayerId() {
        return playerId;
    }

    public int getResult() {
        return result;
    }
}
