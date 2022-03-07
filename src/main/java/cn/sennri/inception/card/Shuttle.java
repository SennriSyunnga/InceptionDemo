package cn.sennri.inception.card;

import cn.sennri.inception.AbcEffect;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.Arrays;

public class Shuttle extends AbcCard {
    public Shuttle() {
        this.effects = Arrays.asList(new GoUpEffect(this), new GoDownEffect(this));
    }


    public static class GoUpEffect extends AbcEffect {

        public GoUpEffect(Card effectSource) {
            super(effectSource);
        }

        /**
         * 效果是否可以发动
         * @param game
         * @return
         */
        @Override
        public boolean isActivable(Game game) {
            Player source = this.source;
            Player.PositionEnum pos =  source.getPos();
            if (pos == Player.PositionEnum.ZERO || pos == Player.PositionEnum.FOUR){
                return false;
            }else{
                return true;
            }
        }

        /**
         * 执行启动效果
         * 这里写效果的处理逻辑
         * @param game 影响对象
         */
        @Override
        public void takeEffect(Game game) {
            Player source = this.source;
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
            return false;
        }

        /**
         * 执行启动效果
         * 这里写效果的处理逻辑
         * @param game 影响对象
         */
        @Override
        public void takeEffect(Game game) {

        }
    }

}
