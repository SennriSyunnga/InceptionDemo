package cn.sennri.inception.player;


import cn.sennri.inception.Effect;
import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.util.Utils;

import java.net.InetAddress;
import java.util.List;

/**
 *
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

    /**
     * 获取当前手卡
     * @return
     */
    List<Card> getHandCards();

    void commonDraw(Deck deck);

    PositionEnum getPos();

    default void active(Effect effect, Player[] targets){
        effect.setSourcePlayer(this);
        effect.setTargets(targets);
    }

    default void setRole(Role role){

    }

    default void refreshItsRole(){
        this.getRole().refresh();
    }

    default Role getRole(){return null;}

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
        ZERO,
        ONE,
        TWO,
        THREE,
        FOUR;

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
    }
}
