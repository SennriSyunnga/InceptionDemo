package cn.sennri.inception.event;

/**
 * 该事件在发动方应该在主机得到pass答复后直接结算，而不必让主机再推给他。
 */
public class ActiveEvent extends AbstractEvent implements Event{
    int subject;
    /**
     * 效果对象 卡 角色 层 或者 无对象
     */
    int[] object;
    /**
     * 用以确认卡牌
     */
    int cardUid;
    /**
     * 是否从手卡发动 影响是否需要手卡减少
     */
    boolean handCardEffect;

    public int getSubject() {
        return subject;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }

    public int[] getObject() {
        return object;
    }

    public void setObject(int[] object) {
        this.object = object;
    }

    public int getCardUid() {
        return cardUid;
    }

    public void setCardUid(int cardUid) {
        this.cardUid = cardUid;
    }

    public boolean isHandCardEffect() {
        return handCardEffect;
    }

    public void setHandCardEffect(boolean handCardEffect) {
        this.handCardEffect = handCardEffect;
    }
}
