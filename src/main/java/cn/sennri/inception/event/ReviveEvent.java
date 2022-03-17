package cn.sennri.inception.event;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 没有实际效果，仅仅用来产生动画。
 */
@NoArgsConstructor
@AllArgsConstructor
public class ReviveEvent extends AbstractEvent{
    /**
     * 主动方
     */
    int subject;

    /**
     * 被复活的玩家
     */
    int object;

    public int getObject() {
        return object;
    }

    public int getSubject() {
        return subject;
    }
}
