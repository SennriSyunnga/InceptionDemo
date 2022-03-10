package cn.sennri.inception.client.view;

import cn.sennri.inception.player.Player;

/**
 * client端可以看见的内容信息
 */
public class PlayerView {

    public PlayerView(Player p){
        this.uid = p.getUid();
        this.name = p.getName();
        this.position = Player.PositionEnum.ONE;
        this.mode = Player.ModeEnum.UP;
        this.status = Player.StatusEnum.ALIVE;
        this.handCardNum = 0;
    }


    /**
     * 玩家自定义昵称，可以重复
     */
    String name;

    public String getName() {
        return name;
    }

    /**
     * webSocket生成的uid，用来区分在当前游戏中的重名玩家
     */
    String uid;

    /**
     * 该玩家手卡剩余量
     */
    int handCardNum;

    /**
     * 位置
     */
    Player.PositionEnum position;

    /**
     * 正反面
     */
    Player.ModeEnum mode;

    /**
     * 存活状态
     */
    Player.StatusEnum status;

    /**
     * 角色序号, 通过本地自己set。
     */
    int role;

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getHandCardNum() {
        return handCardNum;
    }

    public void setHandCardNum(int handCardNum) {
        this.handCardNum = handCardNum;
    }

    public Player.PositionEnum getPosition() {
        return position;
    }

    public void setPosition(Player.PositionEnum position) {
        this.position = position;
    }

    public Player.ModeEnum getMode() {
        return mode;
    }

    public void setMode(Player.ModeEnum mode) {
        this.mode = mode;
    }

    public Player.StatusEnum getStatus() {
        return status;
    }

    public void setStatus(Player.StatusEnum status) {
        this.status = status;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
