package cn.sennri.inception;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.client.view.PlayerView;
import cn.sennri.inception.event.Event;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 因为想要在客户端上没有game实例，只有view，想要在客户端上也做操作控制的话，就一定需要抽象game和view的共通接口
 *
 */
public interface GameInfo {

    public int getDeckRemainNum();

    public List<Card> getGraveyard();

    public List<Card> getExclusionZone() ;

    public List<PlayerInfo> getPlayerInfo() ;

    public Game.Phase getPhase() ;

    public int getTurnOwner() ;

    public AtomicBoolean getIsAsking() ;

    public int getAskingPlayer() ;

    public int getAskedPlayer() ;

    public List<Effect> getEffectStack() ;

    public List<Card> getTempGraveyard() ;

    public List<Event> getEventList() ;

    public Deck getDeck() ;

    interface PlayerInfo{
        public String getUid();

        public void setUid(String uid);

        public int getHandCardNum();

        public Player.PositionEnum getPosition();

        public Player.ModeEnum getMode();

        public Player.StatusEnum getStatus();

        public int getRole();
    }
}
