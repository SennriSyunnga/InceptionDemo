package cn.sennri.inception.card;

import cn.sennri.inception.AbcEffect;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.Collections;

/**
 * 移形换影
 */
public class RoleSwitching extends AbcCard {
    public RoleSwitching(){
        this.cardName = "移形换影";
        this.description = "移形换影";
        this.effects = Collections.singletonList(new RoleSwitchingEffect(this));
    }

    public static class RoleSwitchingEffect extends AbcEffect{

        @Override
        public boolean isActivationLegal(Game game, Player source, int[] targets) {
            if (!super.isActivationLegal(game, source, targets)){
                return false;
            }else{
                int target = targets[0];
                Player other = game.getPlayers()[target];
                if (other == game.getHost()){
                    return false;
                }
                return other.isAlive();
            }
        }

        public RoleSwitchingEffect(Card effectSource) {
            super(effectSource);
        }

        @Override
        public void takeEffect(Game game) {
            int object = this.targets[0];
            Player other = game.getPlayers()[object];
            game.switchRole(this.source, other);
            // 回合结束阶段时如果takeEffect也会添加，所以我们需要在回合结束时置空EndPhaseEffects
            game.getEndPhaseEffects().add(this);
        }
    }
}
