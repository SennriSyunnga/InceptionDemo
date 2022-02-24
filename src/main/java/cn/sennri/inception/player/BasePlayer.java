package cn.sennri.inception.player;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sennri
 */
public class BasePlayer implements Player {

    public AtomicInteger defaultDrawTime = new AtomicInteger(1);

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

    final public int decryptTimes = 1;

    public final InetAddress inetAddress;

    public BasePlayer(InetAddress inetAddress){
        this.status = StatusEnum.ALIVE;
        this.hands = new ArrayList<>();
        this.inetAddress = inetAddress;
        this.pos = PositionEnum.ONE;
    }

    @Override
    public List<Card> getHandCards() {
        return this.hands;
    }

    @Override
    public void draw(Deck deck) {
        hands.add(deck.draw());
    }

    @Override
    public InetAddress getInetAddress() {
        return this.inetAddress;
    }

    @Override
    public void revive(){
        this.status = StatusEnum.ALIVE;
    }

    /**
     * 准备完成
     * @return
     */
    @Override
    public AtomicBoolean isReady() {
        return ready;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BasePlayer)) {
            return false;
        }
        BasePlayer that = (BasePlayer) o;
        return getInetAddress().equals(that.getInetAddress());
    }

    @Override
    public int hashCode() {
        return getInetAddress().hashCode();
    }
}
