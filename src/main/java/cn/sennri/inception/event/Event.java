package cn.sennri.inception.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 标签接口，表征是事件，具体需要反序列化。
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface Event {
}
