package cn.sennri.inception.message;

import cn.sennri.inception.event.Event;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 定义的一类接口。
 * 该接口用于表征只是一类由服务器推送给客户端的更新指令
 * 如回合变更
 * 阶段变更
 * 如卡牌移动
 * 这些原子性的指令。
 */
public interface UpdatePushMessage extends Message{
    @NotEmpty
    List<Event> getEventList();

    default Event getEvent(){
        return getEventList().get(0);
    }

}
