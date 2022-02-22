package cn.sennri.inception.client.view;

import cn.sennri.inception.Effect;
import cn.sennri.inception.card.Card;
import cn.sennri.inception.server.Game;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 场上的总体信息
 */
public class FieldView {
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
    List<PlayerView> playerViewList;

    /**
     * 游戏结束条件
     */
    AtomicBoolean gameOver;

    /**
     * 当前所处阶段
     */
    Game.Phase phase;
    /**
     *  回合主
     */
    int turnOwner;
    /**
     * 是否当前处于询问响应阶段, 若为 true，可以在自己回合外发动对应的效果。
     */
    AtomicBoolean isAsking;
    /**
     *  被询问是否响应的对象
     */
    int askedIndex;

    /**
     * 当前尚未结束的
     */
    List<Effect> effectStack;

    /**
     * 临时区域
     */
    List<Card> tempZone;

    List<Card> useZone;

    List recentEvent;
}
