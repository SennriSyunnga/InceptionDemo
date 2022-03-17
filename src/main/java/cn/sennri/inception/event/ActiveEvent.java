package cn.sennri.inception.event;

import cn.sennri.inception.util.GameUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 该事件在发动方应该在主机得到pass答复后直接结算，而不必让主机再推给他。
 */
@AllArgsConstructor
@NoArgsConstructor
public class ActiveEvent extends AbstractEvent implements Event{
    /**
     * 卡牌的第N个效果
     */
    int effectNum;

    /**
     * 效果主体
     */
    Integer subject;
    /**
     * 效果对象 卡 角色 层 或者 无对象
     */
    int[] object;

    public void setEffectNum(int effectNum) {
        this.effectNum = effectNum;
    }

    public GameUtils.TargetTypeEnum getTargetType() {
        return targetType;
    }

    public void setTargetType(GameUtils.TargetTypeEnum targetType) {
        this.targetType = targetType;
    }

    /**
     * 描述对象的类型，是指定玩家，指定墓地卡牌，或者指定其他的东西
     * todo 应该在Effect当中添加一个TargetType作为校验条件之一
     */
    GameUtils.TargetTypeEnum targetType;
    /**
     * 用以确认卡牌
     */
    int cardUid;
    /**
     * 是否从手卡发动 影响是否需要手卡减少
     */
    boolean handCardEffect;

    public int getEffectNum() {
        return effectNum;
    }

    public Integer getSubject() {
        return subject;
    }

    public void setSubject(Integer subject) {
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
