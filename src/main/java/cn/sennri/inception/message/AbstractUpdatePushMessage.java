package cn.sennri.inception.message;

import cn.sennri.inception.event.Event;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class AbstractUpdatePushMessage extends AbstractMessage implements UpdatePushMessage {
    protected List<Event> eventList;

    @Override
    public @NotEmpty List<Event> getEventList() {
        return eventList;
    }
}
