package cn.sennri.inception.card.role;

import cn.sennri.inception.card.AbcCard;
import cn.sennri.inception.card.Card;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.Arrays;
import java.util.List;

public class BaseRoleCard extends AbcCard implements RoleCard {
    /**
     * 角色拥有者
     * 用于移形换影switch角色卡
     */
    protected Player owner;
    protected Game game;
    protected final int MAX_DECRYPT_TIME = 1;
    protected int decryptTimeThisTurn = 0;

    @Override
    public boolean canDecrypt() {
        // 所在层数无法解锁
        if (game.getLocks()[this.owner.getPos().getLayerNum()] == 0) {
            return false;
        }
        // 还有解锁次数
        return decryptTimeThisTurn < MAX_DECRYPT_TIME;
    }

    @Override
    public void decrypted() {
        decryptTimeThisTurn++;
    }

    public BaseRoleCard(Player owner, Game game) {
        this.owner = owner;
        this.game = game;
    }

    @Override
    public void awakenBy(Player player) {
        Player.PositionEnum pos = this.owner.equals(player) ?
                Player.PositionEnum.ONE : player.getPos();
        game.playerAwaken(this.owner, pos);
    }

    /**
     * 常规复活还是泛用复活？
     */
    @Override
    public boolean revive(Player p, int[] num) {
        // todo 确认发动条件是否满足, Role 可能有自定义的规则；
        if (num.length != 2) {
            return false;
        }
        // todo 确认场地规则
        if (!p.getStatus().equals(Player.StatusEnum.LOST)) {
            return false;
        }
        discard(num);
        game.playerRevive(owner, p);
        p.awakenBy(owner);
        return true;
    }

    /**
     * 计划上应该Player的draw调用这个，使得draw能够被Role感知到，并且能执行对应效果，例如抽到xx并展示，然后可以多抽一张。
     */
    public void draw(int times) {
        game.drawDeck(owner, times);
        // 这里应该根据结果发动具体效果
    }

    @Override
    public void drawInDrawPhase() {
        draw(2);
    }

    @Override
    public boolean discard(int[] nums) {
        List<Card> handCards = this.owner.getHandCards();
        if (nums.length > handCards.size()) {
            // 弃牌数量不能大于手牌总数
            return false;
        } else {
            // 排序
            Arrays.sort(nums);
            if (nums[nums.length - 1] >= handCards.size()) {
                // 弃牌序号不能超过剩余手牌最大序号
                return false;
            }
            game.discard(owner, nums);
            return true;
        }
    }

    @Override
    public boolean canShoot(Player other) {
        return owner.getPos().equals(other.getPos());
    }

    @Override
    public void refresh() {
        decryptTimeThisTurn = 0;
    }

}
