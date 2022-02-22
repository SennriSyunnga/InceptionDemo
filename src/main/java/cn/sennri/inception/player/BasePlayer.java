package cn.sennri.inception.player;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Sennri
 */
public class BasePlayer extends AbcPlayer{

    final public int decryptTimes = 1;

    AtomicBoolean ready;

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
    public InetAddress getInetAddress() {
        return this.inetAddress;
    }

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
