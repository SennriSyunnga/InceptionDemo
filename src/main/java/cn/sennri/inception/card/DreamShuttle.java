package cn.sennri.inception.card;

import cn.sennri.inception.AbcEffect;
import cn.sennri.inception.server.Game;

import java.util.Arrays;

public class DreamShuttle extends AbcCard {
    public DreamShuttle() {
        this.cardName = "梦境穿梭剂";
        this.effects = Arrays.asList(new MoveUpEffect(this), new GoDownEffect(this));
    }

    public static class MoveUpEffect extends AbcEffect {

        public MoveUpEffect(Card effectSource) {
            super(effectSource);
        }

        /**
         * 效果是否可以发动
         * @param game
         * @return
         */
        @Override
        public boolean isActivable(Game game) {
            return source.getPos().canGoUp();
        }

        /**
         * 执行启动效果
         * 这里写效果的处理逻辑
         * @param game 影响对象
         */
        @Override
        public void takeEffect(Game game) {
            // 这里要考虑到是否在连锁中死亡的情况吗？算了吧，不要。
            source.setPos(source.getPos().toNext());
        }
    }

    public static class GoDownEffect extends AbcEffect {

        protected GoDownEffect(Card effectSource) {
            super(effectSource);
        }

        /**
         * 效果是否可以发动
         * @param game
         * @return
         */
        @Override
        public boolean isActivable(Game game) {
            return source.getPos().canGoDown();
        }

        /**
         * 执行启动效果
         * 这里写效果的处理逻辑
         * @param game 影响对象
         */
        @Override
        public void takeEffect(Game game) {
            source.setPos(source.getPos().toPre());
        }
    }

}
