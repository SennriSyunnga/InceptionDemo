package cn.sennri.inception.event;

import cn.sennri.inception.player.Player;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class AwakenEvent extends AbstractEvent{
    /**
     * 被复活的
     */
    int player;

    /**
     *
     */
    Player.PositionEnum position;

    public int getPlayer() {
        return player;
    }

    public Player.PositionEnum getPosition() {
        return position;
    }
}
