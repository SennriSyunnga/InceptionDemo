package cn.sennri.inception.card;

import cn.sennri.inception.Effect;
import cn.sennri.inception.player.Player;

import java.util.List;

public abstract class AbcCard implements Card{
    // todo 是否应该在这里加入标志位： 是否可以在asking阶段发动 是否可以在xx phase阶段发动

    protected String cardName;

    @Override
    public String getCardName() {
        return cardName;
    }

    protected int uid;

    @Override
    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public int getUid() {
        return uid;
    }

    protected Player owner;

    protected String description;

    protected List<Effect> effects;

    public AbcCard(){};

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player newOwner){
        this.owner = newOwner;
    }

    @Override
    public List<Effect> getEffects(){
        return this.effects;
    }

}
