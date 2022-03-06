package cn.sennri.inception.card;

import cn.sennri.inception.AbcEffect;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.Arrays;
import java.util.List;

public class Shoot extends AbcCard implements IShoot{

    public Shoot(){
        this.effects = Arrays.asList(new Effect(this));
    }

    /**
     * 获取效果列表
     * @return
     */
    @Override
    public List<Effect> getEffects() {
        return null;
    }

    /**
     * 获取效果列表
     * @return
     * @param num
     */
    @Override
    public Effect getEffect(int num) {
        return null;
    }

    @Override
    public boolean isActivable(Game game) {
        return false;
    }


    public static class Effect extends AbcEffect{

        protected Effect(Card effectSource) {
            super(effectSource);
        }

        @Override
        public boolean isActivable(Game game) {
            // 这名字可能不太正确 todo
            Player cardOwner = this.getSourcePlayer();
            return game.getTurnOwner().equals(cardOwner) &&
                    game.getPhase().equals(Game.Phase.USE_PHASE);
        }

        @Override
        public void active(Game game, Player[] targets) {
            Player owner = this.source;
            Player target = this.targets[0];
            if (owner.canShoot(target)){
                super.active(game, targets);
            }
        }

        @Override
        public void takeEffect(Game game) {
            Player owner = this.source;
            int ans = owner.rollShootResult();
            Player target = this.targets[0];
            if (ans < 2){
                target.setStatus(Player.StatusEnum.LOST);
            }else if (ans < 5){
                // todo 等待消息 这里先不写实现
                target.setPos(target.getPos().toNext());
            }
        }
    }

}
