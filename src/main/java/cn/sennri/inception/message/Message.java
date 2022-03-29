package cn.sennri.inception.message;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 标签类，用以多态反序列化
 * 可以加入两个default方法
 * id:消息id
 * replyId:在对方系统中的id
 * 加一个接口是 replyAble？
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface Message {
    /**
     * 获取消息反序列化类型
     * @return
     */
    String getType();

    /**
     * 获取消息id编号
     * @return
     */
    Long getMessageId();

    void setMessageId(Long id);
}
