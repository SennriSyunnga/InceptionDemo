package cn.sennri.inception.event;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ShootDeadEvent extends AbstractEvent{
    private int sourceId;
    private int targetId;

    public int getSourceId() {
        return sourceId;
    }

    public int getTargetId() {
        return targetId;
    }
}
