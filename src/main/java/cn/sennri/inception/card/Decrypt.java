package cn.sennri.inception.card;

import cn.sennri.inception.AbcEffect;
import cn.sennri.inception.Effect;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.List;

public class Decrypt extends AbcCard{
    public static class DecryptEffect extends AbcEffect {
        public DecryptEffect(Card effectSource) {
            super(effectSource);
        }

        /**
         * 执行启动效果
         * 这里写效果的处理逻辑
         * @param game 影响对象
         */
        @Override
        public void takeEffect(Game game) {
            // 这里有问题
            game.decryptLock(0);
        }
    }

    public static class AntiDecryptEffect extends AbcEffect {
        public AntiDecryptEffect(Card effectSource) {
            super(effectSource);
        }

        @Override
        public boolean isActivable(Game game) {
            if (!game.getIsAsking().get()){
                return false;
            }
            Player askedPlayer = game.getAskedPlayer().getNode();
            if (askedPlayer != this.effectSource.getOwner()){
                return false;
            }
            List<Effect> effectChain = game.getEffectChain();

            int size = effectChain.size();
            if(size == 0){
                return false;
            }
            Effect lastEffect = effectChain.get(size - 1);
            return lastEffect instanceof DecryptEffect;
        }

        /**
         * 执行启动效果
         * 这里写效果的处理逻辑
         * @param game 影响对象
         */
        @Override
        public void takeEffect(Game game) {
            //todo
        }
    }
}
