package cn.sennri.inception.card.role;

import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

public class HostRoleCard extends BaseRoleCard {
    public HostRoleCard(Player owner, Game game) {
        super(owner, game);
    }

    @Override
    public boolean revive(Player p, int[] num) {
        // 迷失层的玩家才能复活
        if (!p.getStatus().equals(Player.StatusEnum.LOST)){
            return false;
        }
        if (this.owner.equals(p) && Game.Phase.DRAW_PHASE.equals(game.getPhase())){
            // 可以无需弃牌复活
            if (num != null){
                throw new IllegalArgumentException("梦主在抽牌阶段复活无需弃牌");
            }
        }else{
            if (num.length != 2){
                return false;
            }
            // 需要弃牌

            // todo 确认场地规则，如果场地要求卡牌必须是xx，就得在此检查
            discard(num);
        }
        // 结果结算
        p.awakenBy(owner);
        return true;
    }

}
