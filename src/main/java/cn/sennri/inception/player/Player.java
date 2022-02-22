package cn.sennri.inception.player;


import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public interface Player {

    List<Card> getHandCards();
    InetAddress getInetAddress();

    /**
     * 准备完成
     * @return
     */
    AtomicBoolean isReady();
    public void activeBeginningPhaseEffect();
    public void doPreDraw();
    public void drawCards(Deck deck);
    public void doAfterDraw();
    
    public void activeEndPhaseEffect();

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
