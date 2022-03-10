package cn.sennri.inception.event;

/**
 * 该事件将asking置为true
 * 并将asking对象指向
 */
public class AskingEvent extends AbstractEvent {
    int fromPlayer;
    int toPlayer;

    public AskingEvent(int from, int to){
        super();
        this.fromPlayer = from;
        this.toPlayer = to;
    }
}
