package cn.sennri.inception.field;
import cn.sennri.inception.card.Card;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CountDownLatch;


public abstract class AbcDeck {
    /**
     * 获取deck的倒计时Latch，以便于Game保存
     *
     * todo 这个设计不行，因为这样的设计在卡片放回卡顶时无法加计数
     * @return
     */
    abstract CountDownLatch getHostWinCondition();
    final static int MAX_CAPABILITY = 70;
    public Deque<Card> deck = new ArrayDeque<>(MAX_CAPABILITY);
}
