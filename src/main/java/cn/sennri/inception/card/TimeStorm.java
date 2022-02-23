package cn.sennri.inception.card;

import cn.sennri.inception.AbcEffect;
import cn.sennri.inception.Effect;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.ArrayDeque;
import java.util.List;

/**
 * 时间风暴
 */
public class TimeStorm {


    public static class DiscardEffect extends AbcEffect{

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

        }


    }
}
