package cn.sennri.inception.message;

/**
 * 客户端发动消息
 */
public class ClientActiveMessage implements Message{
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Integer getHandCardNumber() {
        return handCardNumber;
    }

    public void setHandCardNumber(Integer handCardNumber) {
        this.handCardNumber = handCardNumber;
    }

    public Integer getEffectNumber() {
        return effectNumber;
    }

    public void setEffectNumber(Integer effectNumber) {
        this.effectNumber = effectNumber;
    }

    public Integer[] getTargetPlayerNumber() {
        return targetPlayerNumber;
    }

    public void setTargetPlayerNumber(Integer[] targetPlayerNumber) {
        this.targetPlayerNumber = targetPlayerNumber;
    }

    /**
     * 自增消息序数
     */
    Long messageId;
    /**
     * 手牌中的卡片序数
     */
    Integer handCardNumber;

    Integer effectNumber;
    /**
     * 玩家对象序号
     */
    Integer[] targetPlayerNumber;

    private String type = "ClientActiveMessage";

    /**
     * 获取消息反序列化类型
     * @return
     */
    @Override
    public String getType() {
        return type;
    }
}
