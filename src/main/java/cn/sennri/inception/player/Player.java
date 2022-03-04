package cn.sennri.inception.player;


import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.util.Utils;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    void revive();


    void draw(Deck deck);

    /**
     * 获取当前手卡
     * @return
     */
    List<Card> getHandCards();



    InetAddress getInetAddress();


    /**
     * 准备完成
     * @return
     */
    AtomicBoolean isReady();

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

    }
}
