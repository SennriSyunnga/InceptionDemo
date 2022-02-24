package cn.sennri.inception.card;

import cn.sennri.inception.AbcEffect;
import cn.sennri.inception.Effect;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.server.Game;

import java.util.List;

/**
 * 时间风暴
 */
public class TimeStorm extends AbcCard {

    @Override
    public List<Effect> getEffects() {
        return null;
    }

    @Override
    public Effect getEffect(int num) {
        return null;
    }

    @Override
    public boolean isActivable(Game game) {
        return false;
    }

    public static class DiscardEffect extends AbcEffect{

        protected DiscardEffect(Card effectSource) {
            super(effectSource);
        }

        /**
         * 通过effectStack确定是否前面发动的卡可以被你响应
         * 是否存在骰子事件
         * 是否存在
         * 墓地里是否有需要的卡
         * 是否有骰子结果
         * @param game
         * @return
         */
        @Override
        public boolean isActivable(Game game) {
            return false;
        }

        /**
         * 执行启动效果
         * 这里写效果的处理逻辑
         * @param game
         */
        @Override
        public void takeEffect(Game game) {
            List<Card> tempGraveyard = game.getTempGraveyard();
            List<Card> graveyard = game.getGraveyard();
            Deck deck = game.getDeck();
            for (int i = 0;i < 10;i++){
                deck.abandon(graveyard, tempGraveyard);
            }
        }
    }
}
