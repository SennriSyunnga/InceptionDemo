package cn.sennri.inception.message;

import cn.sennri.inception.event.Event;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class UpdatePushMessageImpl extends AbstractMessage implements UpdatePushMessage {
    protected List<Event> eventList;
    @Override
    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    @Override
    public @NotEmpty List<Event> getEventList() {
        return eventList;
    }
}
