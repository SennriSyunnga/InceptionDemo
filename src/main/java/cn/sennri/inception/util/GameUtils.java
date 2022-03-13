package cn.sennri.inception.util;

import java.util.concurrent.ThreadLocalRandom;

public class GameUtils {
    final static ThreadLocalRandom random = ThreadLocalRandom.current();
    public static int roll(){
        return random.nextInt(6) + 1;
    }

    /**
     * 可能需要的效果的指定对象
     */
    enum TargetTypeEnum{
        /**
         * 不取对象的效果
         */
        NONE,
        /**
         * 针对玩家的效果
         */
        PLAYER,
        /**
         * 将对象解读为墓地里的卡
         */
        GRAVEYARD_CARD,
        /**
         * 针对某个玩家的手牌
         */
        HAND_CARD
    }
}
