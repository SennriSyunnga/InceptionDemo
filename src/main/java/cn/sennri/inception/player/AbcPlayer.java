package cn.sennri.inception.player;

import cn.sennri.inception.card.Card;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbcPlayer implements Player {
    public AtomicBoolean ready;

    public void setReady(AtomicBoolean ready) {
        this.ready = ready;
    }

    public AtomicBoolean getReady() {
        return ready;
    }

    /**
     * 存活或者死亡
     */
    public StatusEnum status;
    /**
     * 手牌
     */
    public List<Card> hands;
    /**
     * 正反面
     */
    public ModeEnum mode;
    /**
     * 位置 1 2 3 4层
     */
    public PositionEnum pos;


}
