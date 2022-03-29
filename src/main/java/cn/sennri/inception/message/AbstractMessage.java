package cn.sennri.inception.message;

public abstract class AbstractMessage implements Message{
    public AbstractMessage(){
        // getClass会根据运行期类型来为type设置值
        this.type = this.getClass().getSimpleName();
    }

    /**
     * 标记自身id信息，对于特定种类的消息，应该将自身录入map里等待回应消息
     */
    protected Long messageId;

    @Override
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    @Override
    public Long getMessageId() {
        return messageId;
    }

    /**
     * 标记反序列化类型
     */
    protected final String type;

    @Override
    public String getType(){
        return this.type;
    }

}
