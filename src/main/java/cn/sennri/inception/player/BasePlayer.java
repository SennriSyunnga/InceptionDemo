package cn.sennri.inception.player;

import cn.sennri.inception.Effect;
import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.server.Game;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sennri
 */
public class BasePlayer implements Player {

    public Role role;

    public Game game;

    /**
     * webSocketId标识
     */
    public String uid;

    /**
     * 用户昵称
     */
    public String name;

    public BasePlayer(Game game, String uid , String nickName){
        this.game = game;
        this.name = nickName;
        this.uid = uid;
        this.role = new BaseRole(this, game);
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

    public BasePlayer(InetAddress inetAddress){
        this.status = StatusEnum.ALIVE;
        this.hands = new ArrayList<>();
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
    public void awakenBy(Player player){
        this.role.awakenBy(player);
    }

    @Override
    public boolean revive(Player p, int[] num) {
        return role.revive(p,  num);
    }

    @Override
    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public Role getRole() {
        return role;
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
    public void active(Effect effect, int[] targets) {
        effect.setSourcePlayer(this);
        effect.setTargets(targets);
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
        return uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }
}
