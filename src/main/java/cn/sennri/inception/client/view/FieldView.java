package cn.sennri.inception.client.view;

import cn.sennri.inception.Effect;
import cn.sennri.inception.card.Card;
import cn.sennri.inception.event.*;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.message.UpdatePushMessage;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 场上的总体信息
 */
@Slf4j
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

    /**
     * 生成selfPointer，用于标识本玩家
     * 主要用于初始化内部的各种域
     *
     * @param name
     */
    public void initialize(String name) {
        PlayerView[] views = this.playerViews;
        for (int i = 0, viewsLength = views.length; i < viewsLength; i++) {
            PlayerView p = views[i];
            if (p.getName().equals(name)) {
                this.selfPointer = i;
                break;
            }
        }
        this.graveyard = new ArrayList<>();
        this.exclusionZone = new ArrayList<>();
        this.isAsking = new AtomicBoolean(false);
        this.tempGraveyard = new ArrayList<>();
        this.effectStack = new ArrayList<>();
        this.eventList = new ArrayList<>();
    }

    /**
     * 用于反序列化的无参构造器
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
     * 回合主指针
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
    @Deprecated
    List<Card> tempGraveyard;

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
            int playerId = ((RollEvent) e).getPlayerId();
            int result = ((RollEvent) e).getResult();
            log.info("玩家{}摇出了结果{}", playerViews[playerId].getName(), result);
            // todo 这里执行回显事件
            this.eventList.add(e);
        } else if (e instanceof DrawEvent) {
            DrawEvent drawEvent = (DrawEvent) e;
            int subject = drawEvent.getSubject();
            int cardNum = drawEvent.getCount();
            playerViews[subject].handCardNum += cardNum;
            log.info("玩家{}从卡顶抽出了{}张牌。", playerViews[subject].getName(), cardNum);
            this.eventList.add(e);
        } else if (e instanceof MoveEvent) {
            int subject = ((MoveEvent) e).getSubject();
            boolean up = ((MoveEvent) e).getUp();
            PlayerView view = playerViews[subject];
            Player.PositionEnum originalPos = view.getPosition();
            if (up) {
                view.setPosition(originalPos.toNext());
                log.info("玩家{}从{}层向上移动到了第{}层", view.getName(), originalPos.getLayerNum(), view.getPosition().getLayerNum());
            } else {
                view.setPosition(originalPos.toPre());
                log.info("玩家{}从{}层向下移动到了第{}层", view.getName(), originalPos.getLayerNum(), view.getPosition().getLayerNum());
            }
            this.eventList.add(e);
        } else if (e instanceof DiscardEvent) {
            int subject = ((DiscardEvent) e).getSubject();
            int[] cardIds = ((DiscardEvent) e).getCardIds();
            PlayerView playerView = playerViews[subject];
            playerView.handCardNum -= cardIds.length;
            for (int id : cardIds) {
                Card card = deck.getUidToCard()[id];
                this.graveyard.add(card);
                this.tempGraveyard.add(card);
                log.info("玩家{}从手牌弃置了一张{}", playerView.getName(), card.getCardName());
            }
            this.eventList.add(e);
        } else if (e instanceof ActiveEvent) {
            boolean handCardEffect = ((ActiveEvent) e).isHandCardEffect();
            // 说明由玩家发动的效果
            if (handCardEffect){
                int subject = ((ActiveEvent) e).getSubject();
                PlayerView playerView = playerViews[subject];
                int[] objects = ((ActiveEvent) e).getObject();
                int cardId = ((ActiveEvent) e).getCardUid();
                int effectNum = ((ActiveEvent) e).getEffectNum();
                Card card = deck.getCardByUid(cardId);
                // 回调，显示这张卡。
                graveyard.add(card);
                this.tempGraveyard.add(card);
                if(objects == null){
                    log.info("玩家{}从手牌中发动了卡牌{}的效果{}",
                            playerView.getName(),
                            card.getCardName(),
                            card.getEffect(effectNum).getDescription());
                }else{
                    log.info("玩家{}以{}为对象从手牌中发动了卡牌{}的效果{}",
                            playerView.getName(),
                            objects,
                            card.getCardName(),
                            card.getEffect(effectNum).getDescription());
                }
            }else{
                Integer subject = ((ActiveEvent) e).getSubject();
                if (subject == null){
                    // 墓地必发效果
                    int cardId = ((ActiveEvent) e).getCardUid();
                    int effectNum = ((ActiveEvent) e).getEffectNum();
                    Card card = deck.getCardByUid(cardId);
                    log.info("卡牌{}发动了效果{}",
                            card.getCardName(),
                            card.getEffect(effectNum).getDescription());
                }else{
                    // 不会送去墓地的效果
                    PlayerView playerView = playerViews[subject];
                    int cardId = ((ActiveEvent) e).getCardUid();
                    int effectNum = ((ActiveEvent) e).getEffectNum();
                    Card card = deck.getCardByUid(cardId);
                    log.info("玩家{}发动了卡牌{}的效果{}",
                            playerView.getName(),
                            card.getCardName(),
                            card.getEffect(effectNum).getDescription());
                }
            }
            this.eventList.add(e);
        } else if (e instanceof TurnEndEvent) {
            this.turnOwner = ((TurnEndEvent) e).getNextTurnPlayerId();
            this.phase = Game.Phase.DRAW_PHASE;
            this.eventList.clear();
        }
    }
}
