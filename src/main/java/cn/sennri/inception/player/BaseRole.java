package cn.sennri.inception.player;

import cn.sennri.inception.card.Card;
import cn.sennri.inception.field.Deck;
import cn.sennri.inception.server.Game;

import java.util.Arrays;
import java.util.List;

public class BaseRole implements Role{
    /**
     * 角色拥有者
     * 用于移形换影switch角色卡
     */
    protected Player owner;
    protected Game game;
    protected final int MAX_DECRYPT_TIME = 1;
    protected int decryptTimeThisTurn = 0;

    boolean canDecrypt(){
        return decryptTimeThisTurn <MAX_DECRYPT_TIME;
    }

    @Override
    public void decrypted(){
        decryptTimeThisTurn++;
    }

    public BaseRole(Player owner, Game game){
       this.owner = owner;
       this.game = game;
    }

    @Override
    public void awakenBy(Player player) {
        if (this.owner.equals(player)){
            // 复活至一层
            owner.setPos(Player.PositionEnum.ONE);
        }else{
            // 复活至同层
            owner.setPos(player.getPos());
        }
        owner.setStatus(Player.StatusEnum.ALIVE);
    }

    /**
     * 常规复活还是泛用复活？
     */
    @Override
    public boolean revive(Player p, int[] num) {
        // todo 确认发动条件是否满足, Role 可能有自定义的规则；
        if (num.length != 2){
            return false;
        }
        // todo 确认场地规则
        if (!p.getStatus().equals(Player.StatusEnum.LOST)){
            return false;
        }
        discard(num);
        p.awakenBy(owner);
        return true;
    }

    @Override
    public void commonDraw(Deck deck) {
        for (int i = 0; i < 2;i++){
            this.owner.draw(deck);
        }
    }

    @Override
    public boolean discard(int[] nums) {
        List<Card> handCards = this.owner.getHandCards();
        if (nums.length > handCards.size()){
            // 弃牌数量不能大于手牌总数
            return false;
        }else{
            // 排序
            Arrays.sort(nums);
            if (nums[nums.length - 1] >= handCards.size()){
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
