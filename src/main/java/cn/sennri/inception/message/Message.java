package cn.sennri.inception.message;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 标签类，用以多态反序列化
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface Message {
    /**
     * 获取消息反序列化类型
     * @return
     */
    String getType();
}
