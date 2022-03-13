package cn.sennri.inception.card;

import cn.sennri.inception.AbcEffect;
import cn.sennri.inception.server.Game;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 时间风暴
 */
public class TimeStorm extends AbcCard {
    public TimeStorm(){
        this.cardName = "时间风暴";
        this.effects = Collections.singletonList(new DiscardEffect(this));
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
            if (super.isActivable(game)){
                return true;
            }
            List<Card> tempGraveyard = game.getTempGraveyard();
            if (tempGraveyard.contains(this.effectSource)){
                return true;
            }
            return false;
        }

        /**
         * 执行启动效果
         * 这里写效果的处理逻辑
         * @param game
         */
        @Override
        public void takeEffect(Game game) {
            game.deckAbandonCard(10);
            game.vanish(this.effectSource, game.getTempGraveyard());
        }
    }
}
