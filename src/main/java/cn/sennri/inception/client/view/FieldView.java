package cn.sennri.inception.client.view;

import cn.sennri.inception.Effect;
import cn.sennri.inception.card.Card;
import cn.sennri.inception.event.*;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.message.UpdatePushMessage;
import cn.sennri.inception.server.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 场上的总体信息
 */
public class FieldView {
    /**
     * 指向自己
     */
    int selfPointer;

    public FieldView(Game game) {
        this.deckRemainNum = game.getDeck().size();
        this.isAsking = new AtomicBoolean(false);
        this.turnOwner = 0;
        this.phase = Game.Phase.DRAW_PHASE;
        this.playerViews = Arrays.stream(game.getPlayers()).map(PlayerView::new).toArray(PlayerView[]::new);
        this.deck = game.getDeck();
    }

    public void initialize() {
        this.graveyard = new ArrayList<>();
        this.exclusionZone = new ArrayList<>();
        this.isAsking = new AtomicBoolean(false);
        this.tempGraveyard = new ArrayList<>();
        this.effectStack = new ArrayList<>();
        this.eventList = new ArrayList<>();
    }

    public void initialize(String name) {
        PlayerView[] views = this.playerViews;
        for (int i = 0, viewsLength = views.length; i < viewsLength; i++) {
            PlayerView p = views[i];
            if (p.getName().equals(name)) {
                this.selfPointer = i;
                break;
            }
        }
        initialize();
    }

    /**
     * 用于反序列化
     */
    public FieldView() {
    }

    /**
     * 卡堆剩余量
     */
    int deckRemainNum;
    /**
     * 墓地列表
     */
    List<Card> graveyard;
    /**
     * 除外区列表
     */
    List<Card> exclusionZone;

    /**
     * 可见的玩家信息
     */
    PlayerView[] playerViews;

    /**
     * 当前所处阶段
     */
    Game.Phase phase;

    /**
     *  回合主指针
     */
    int turnOwner;

    /**
     * 是否当前处于询问响应阶段, 若为 true，可以在自己回合外发动对应的效果。
     */
    AtomicBoolean isAsking;

    /**
     * 当前执行动作并询问玩家的序号
     */
    int askingPlayer;

    /**
     * 当前被询问玩家的序号
     */
    int askedPlayer;

    /**
     * 当前尚未结束的
     */
    List<Effect> effectStack;

    /**
     * 临时墓地区域显示区域
     */
    List<Card> tempGraveyard;

    /**
     * 出牌显示区域
     */
    List<Card> playArea;

    /**
     * 本次连锁上的事件
     */
    List<Event> eventList;


    Deck deck;

    public void consumeUpdateMessage(UpdatePushMessage m) {
        List<Event> newEvenList = m.getEventList();
        for (Event event : newEvenList) {
            executeEvent(event);
        }
    }

    void executeEvent(Event e) {
        if (e instanceof RollEvent) {
            this.eventList.add(e);
            // todo
        } else if (e instanceof DrawEvent) {
            DrawEvent drawEvent = (DrawEvent) e;
            int subject = drawEvent.getSubject();
            int cardNum = drawEvent.getCount();
            playerViews[subject].handCardNum += cardNum;
            this.eventList.add(e);
        } else if (e instanceof MoveEvent) {
            int subject = ((MoveEvent) e).getSubject();
            boolean up = ((MoveEvent) e).getUp();
            PlayerView view = playerViews[subject];
            if (up) {
                view.setPosition(view.getPosition().toNext());
            } else {
                view.setPosition(view.getPosition().toPre());
            }
            this.eventList.add(e);
        } else if (e instanceof DiscardEvent) {
            int subject = ((DiscardEvent) e).getSubject();
            int[] cardIds = ((DiscardEvent) e).getCardIds();
            PlayerView playerView = playerViews[subject];
            playerView.handCardNum -= cardIds.length;
            for (int id : cardIds) {
                Card card = deck.getUidToCard()[id];
                this.tempGraveyard.add(card);
                this.graveyard.add(card);
            }
            this.eventList.add(e);
        }
    }
}
