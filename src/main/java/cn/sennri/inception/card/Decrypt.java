package cn.sennri.inception.card;

import cn.sennri.inception.AbcEffect;
import cn.sennri.inception.Effect;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.Arrays;
import java.util.List;

public class Decrypt extends AbcCard {
    public Decrypt() {
        this.effects = Arrays.asList(new DecryptEffect(this), new AntiDecryptEffect(this));
    }

    public static class DecryptEffect extends AbcEffect {
        public DecryptEffect(Card effectSource) {
            super(effectSource);
        }

        @Override
        public boolean isActivable(Game game) {
            if (!super.isActivable(game)){
                return false;
            }else{
                Player sourcePlayer = this.getSourcePlayer();
                return sourcePlayer.getRole().canDecrypt();
            }
        }

        /**
         * 执行启动效果
         * 这里写效果的处理逻辑
         * @param game 影响对象
         */
        @Override
        public void takeEffect(Game game) {
            game.decryptLock(this.targets[0]);
            this.getSourcePlayer().getRole().decrypted();
        }
    }

    public static class AntiDecryptEffect extends AbcEffect {
        public AntiDecryptEffect(Card effectSource) {
            super(effectSource);
        }

        @Override
        public boolean isActivable(Game game) {
            if (!game.getIsAsking().get()) {
                return false;
            }
            Player askedPlayer = game.getAskedPlayer().getNode();
            if (askedPlayer != this.effectSource.getOwner()) {
                return false;
            }
            List<Effect> effectChain = game.getEffectChain();

            int size = effectChain.size();
            if (size == 0) {
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
            List<Effect> effectChain = game.getEffectChain();
            // 禁用解封
            effectChain.get(targets[0]).setDeactivated();
        }
    }
}
