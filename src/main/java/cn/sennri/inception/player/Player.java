package cn.sennri.inception.player;


import cn.sennri.inception.Effect;
import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.util.Utils;

import java.util.List;

/**
 *  玩家行为接口 涉及到角色特殊效果的，需要通过role代理实现
 */
public interface Player {
    /**
     * 产生roll点结果
     * @return
     */
    default int roll(){
        return Utils.roll();
    }

    /**
     * 产生Shoot卡片roll结果
     * @return
     */
    default int rollShootResult(){
        return roll();
    }

    /**
     * 复活
     */
    void awakenBy(Player player);

    /**
     * 复活一个对象
     */
    boolean revive(Player p, int[] num);

    void setStatus(StatusEnum status);

    void setPos(PositionEnum pos);

    StatusEnum getStatus();

    /**
     * 这里可以设置自己抽到什么卡的回调。
     * @param deck
     */
    void draw(Deck deck);

    String getName();

    /**
     * 获取当前手卡
     * @return
     */
    List<Card> getHandCards();

    void commonDraw(Deck deck);

    PositionEnum getPos();

    /**
     * 获取uid信息
     * @return
     */
    String getUid();

    default void setRole(Role role){

    }

    /**
     * 弃牌
     * @param target
     * @return
     */
    boolean discard(int[] target);

    /**
     * 重置role中的回合状态
     */
    default void refreshItsRole(){
        this.getRole().refresh();
    }

    default Role getRole(){return null;}

    void active(Effect effect, int[] targets);

    /**
     * 是否打得到
     * @param other
     * @return
     */
    default boolean canShoot(Player other) {
        return this.getPos().equals(other.getPos());
    }

    enum StatusEnum {
        /**
         * 存活状态
         */
        ALIVE,
        /**
         * 迷失层
         */
        LOST;
    }

    enum ModeEnum {
        /**
         * 正面朝上
         */
        UP,
        /**
         * 背面朝上
         */
        DOWN
    }

    enum PositionEnum {
        /**
         * 迷失层
         */
        ZERO(0),
        /**
         * 1
         */
        ONE(1),
        /**
         * 2
         */
        TWO(2),
        /**
         * 3
         */
        THREE(3),
        /**
         * 4
         */
        FOUR(4);

        final int layerNum;

        PositionEnum(int num){
            this.layerNum = num;
        }

        public int getLayerNum() {
            return layerNum;
        }

        public PositionEnum toNext(){
            switch (this){
                case ONE:
                    return TWO;
                case TWO:
                    return THREE;
                case THREE:
                    return FOUR;
                default:
                    return null;
            }
        }

        public boolean canGoUp(){
            return this != Player.PositionEnum.ZERO && this != Player.PositionEnum.FOUR;
        }

        public boolean canGoDown(){
            return this != Player.PositionEnum.ZERO && this != Player.PositionEnum.ONE;
        }

        public PositionEnum toPre(){
            switch (this){
                case TWO:
                    return ONE;
                case THREE:
                    return TWO;
                case FOUR:
                    return THREE;
                default:
                    return null;
            }
        }
    }
}
