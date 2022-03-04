package cn.sennri.inception.message;

/**
 * 服务端答复消息
 */
public class ServerAnswerActiveMessage extends AbstractReplyMessage<Boolean> implements Message{
    @Override
    public String getType() {
        return type;
    }

    @Override
    public Boolean getReply() {
        return reply;
    }

    @Override
    public Long getMessageId() {
        return messageId;
    }

    @Override
    public void setReply(Boolean reply) {
        this.reply = reply;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
}
