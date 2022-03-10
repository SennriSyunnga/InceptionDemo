package cn.sennri.inception.event;

public class MoveEvent extends AbstractEvent {
    Integer subject;
    Boolean up;

    public Boolean getUp() {
        return up;
    }

    public Integer getSubject() {
        return subject;
    }
}
