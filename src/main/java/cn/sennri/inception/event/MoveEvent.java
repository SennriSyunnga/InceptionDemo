package cn.sennri.inception.event;

public class MoveEvent extends AbstractEvent {
    /**
     * 主体
     */
    Integer subject;
    Boolean up;

    public Boolean getUp() {
        return up;
    }

    public Integer getSubject() {
        return subject;
    }
}
