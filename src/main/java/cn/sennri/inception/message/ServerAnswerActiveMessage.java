package cn.sennri.inception.message;

/**
 * 服务端答复消息
 */
public class ServerAnswerActiveMessage implements Message{
    /**
     * 获取消息反序列化类型
     * @return
     */
    private String type = "ServerAnswerActiveMessage";

    @Override
    public String getType() {
        return type;
    }

    private Boolean reply;

    public Boolean getReply() {
        return reply;
    }

    private Long messageId;

    public Long getMessageId() {
        return messageId;
    }

    public void setReply(Boolean reply) {
        this.reply = reply;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
}
