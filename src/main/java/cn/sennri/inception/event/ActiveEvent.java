package cn.sennri.inception.event;

/**
 * 该事件在发动方应该在主机得到pass答复后直接结算，而不必让主机再推给他。
 */
public class ActiveEvent {
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
}
