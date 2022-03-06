package cn.sennri.inception.player;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.server.Game;

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

    public Role role;

    public Game game;

    public BasePlayer(Game game, InetAddress inetAddress){
        this.game = game;
        this.role = new BaseRole(this, game);
        this.inetAddress = inetAddress;
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
    public void commonDraw(Deck deck) {
        this.role.commonDraw(deck);
    }

    boolean discard(int[] cardNum){
        return this.role.discard(cardNum);
    }


    @Override
    public InetAddress getInetAddress() {
        return this.inetAddress;
    }

    @Override
    public void awakenBy(Player player){
        this.role.awakenBy(player);
    }

    @Override
    public boolean revive(Player p, int[] num) {
        return role.revive(p,  num);
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public void setMode(ModeEnum mode) {
        this.mode = mode;
    }

    @Override
    public void setPos(PositionEnum pos) {
        this.pos = pos;
    }

    @Override
    public StatusEnum getStatus() {
        return this.status;
    }

    @Override
    public PositionEnum getPos() {
        return this.pos;
    }

    @Override
    public boolean canShoot(Player other) {
        return this.role.canShoot(other);
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
