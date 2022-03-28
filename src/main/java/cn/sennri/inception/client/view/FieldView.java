package cn.sennri.inception.client.view;

import cn.sennri.inception.Effect;
import cn.sennri.inception.GameInfo;
import cn.sennri.inception.card.Card;
import cn.sennri.inception.event.*;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.message.EffectChainCleanMessage;
import cn.sennri.inception.message.Message;
import cn.sennri.inception.message.ServerEventCleanMessage;
import cn.sennri.inception.message.UpdatePushMessage;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;
import cn.sennri.inception.util.GameUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 客户端可见的场上的总体信息
 */
@Slf4j
public class FieldView implements GameInfo {
    /**
     * 指向自己
     */
    int selfPointer;

    List<Card> myHandCards = new ArrayList<>();

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
        this.myHandCards = new ArrayList<>();
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
    public List<Event> eventList;

    Deck deck;

    public void consumeMessage(Message m){
        if (m instanceof ServerEventCleanMessage){
            this.eventList.clear();
        }else if (m instanceof EffectChainCleanMessage){
            this.effectStack.clear();
        }
    }


    public void consumeUpdateMessage(UpdatePushMessage m) {
        List<Event> newEvenList = m.getEventList();
        for (Event event : newEvenList) {
            executeEvent(event);
        }
    }

    private Card[] generateCardArray(int[] nums, List<Card> graveyard){
        int len = nums.length;
        Card[] cards = new Card[len];
        for(int i = 0;i < len; i++){
            cards[i] = graveyard.get(nums[i]);
        }
        return cards;
    }

    private String[] generatePlayerArray(int[] nums, PlayerView[] playerViews){
        int len = nums.length;
        String[] playerNames = new String[len];
        for(int i = 0;i < len; i++){
            playerNames[i] = playerViews[nums[i]].getName();
        }
        return playerNames;
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
            if (subject == selfPointer){
                for (int id : cardIds) {
                    Card card = deck.getUidToCard()[id];
                    this.graveyard.add(card);
                    this.tempGraveyard.add(card);
                    this.myHandCards.remove(card);
                    log.info("玩家{}从手牌弃置了一张{}", playerView.getName(), card.getCardName());
                }
            }else{
                for (int id : cardIds) {
                    Card card = deck.getUidToCard()[id];
                    this.graveyard.add(card);
                    this.tempGraveyard.add(card);
                    log.info("玩家{}从手牌弃置了一张{}", playerView.getName(), card.getCardName());
                }
            }
            this.eventList.add(e);
        } else if (e instanceof ActiveEvent) {
            int cardId = ((ActiveEvent) e).getCardUid();
            // 回调，显示这张卡。
            Card card = deck.getCardByUid(cardId);
            boolean handCardEffect = ((ActiveEvent) e).isHandCardEffect();
            // 说明由玩家发动的效果
            if (handCardEffect){
                int subject = ((ActiveEvent) e).getSubject();
                PlayerView playerView = playerViews[subject];
                int[] objects = ((ActiveEvent) e).getObject();

                int effectNum = ((ActiveEvent) e).getEffectNum();

                Effect effect = card.getEffect(effectNum);
                effectStack.add(effect);
                GameUtils.TargetTypeEnum targetType = effect.getTargetType();

                this.graveyard.add(card);
                this.tempGraveyard.add(card);
                // 不取对象
                if(objects == null){
                    log.info("玩家{}从手牌中发动了卡牌{}的效果{}",
                            playerView.getName(),
                            card.getCardName(),
                            card.getEffect(effectNum).getDescription());
                }else{
                    switch (targetType) {
                        case GRAVEYARD_CARD:
                            log.info("玩家{}以墓地的{}为对象从手牌中发动了卡牌{}的效果{}",
                                    playerView.getName(),
                                    Arrays.toString(generateCardArray(objects, graveyard)),
                                    card.getCardName(),
                                    card.getEffect(effectNum).getDescription());
                            break;
                        case PLAYER:
                            log.info("玩家{}以玩家{}为对象从手牌中发动了卡牌{}的效果{}",
                                    playerView.getName(),
                                    Arrays.toString(generatePlayerArray(objects, playerViews)),
                                    card.getCardName(),
                                    card.getEffect(effectNum).getDescription());
                            break;
                        default:
                    }
                }
            }else{
                Integer subject = ((ActiveEvent) e).getSubject();
                // 没有发动玩家
                if (subject == null){
                    // 必发效果？
                    int effectNum = ((ActiveEvent) e).getEffectNum();
                    log.info("卡牌{}发动了效果{}",
                            card.getCardName(),
                            card.getEffect(effectNum).getDescription());
                }else{
                    // 不会送去墓地的效果
                    PlayerView playerView = playerViews[subject];
                    int effectNum = ((ActiveEvent) e).getEffectNum();
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


    @Override
    public int getDeckRemainNum() {
        return deckRemainNum;
    }

    @Override
    public List<Card> getGraveyard() {
        return graveyard;
    }

    @Override
    public List<Card> getExclusionZone() {
        return exclusionZone;
    }

    @Override
    public List<PlayerInfo> getPlayerInfo() {
        return Arrays.asList(playerViews);
    }

    @Override
    public Game.Phase getPhase() {
        return phase;
    }

    @Override
    public int getTurnOwner() {
        return turnOwner;
    }

    @Override
    public AtomicBoolean getIsAsking() {
        return isAsking;
    }

    @Override
    public int getAskingPlayer() {
        return askingPlayer;
    }

    @Override
    public int getAskedPlayer() {
        return askedPlayer;
    }

    @Override
    public List<Effect> getEffectStack() {
        return effectStack;
    }

    @Override
    public List<Card> getTempGraveyard() {
        return tempGraveyard;
    }

    @Override
    public List<Event> getEventList() {
        return eventList;
    }

    @Override
    public Deck getDeck() {
        return deck;
    }
}
