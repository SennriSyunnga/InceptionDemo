package cn.sennri.inception.card;

import cn.sennri.inception.AbcEffect;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.Collections;
import java.util.List;

public class Shoot extends AbcCard implements IShoot {

    public Shoot() {
        this.cardName = "Shoot";
        this.effects = Collections.singletonList(new Effect(this));
    }

    /**
     * 获取效果列表
     *
     * @return
     */
    @Override
    public List<cn.sennri.inception.Effect> getEffects() {
        return null;
    }

    @Override
    public boolean isActivable(Game game) {
        return effects.get(0).isActivable(game);
    }


    public static class Effect extends AbcEffect {

        protected Effect(Card effectSource) {
            super(effectSource);
        }

        @Override
        public boolean isActivable(Game game) {
            // 这名字可能不太正确 todo
            Player cardOwner = this.getSourcePlayer();
            if (!game.getTurnOwner().equals(cardOwner)) {
                return false;
            }
            if (!game.getPhase().equals(Game.Phase.USE_PHASE)) {
                return false;
            }
            // 有一个能射就能发动
            for (Player p : game.getPlayers()) {
                if (source.canShoot(p)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void takeEffect(Game game) {
            int ans = this.source.rollShootResult();
            Player target = game.getPlayers()[targets[0]];
            if (ans < 2) {
                game.shootDead(source, target);
            } else if (ans < 5) {
                // todo 发送一个消息给当前回合主客户端 让客户端触发询问动作
                boolean go;
                target.setPos(target.getPos().toNext());
            }
        }
    }

}
