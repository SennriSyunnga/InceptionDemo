package cn.sennri.inception.card;

import cn.sennri.inception.Effect;
import cn.sennri.inception.player.Player;

import java.util.List;

public abstract class AbcCard implements Card{

    protected Player owner;

    protected String description;

    protected List<Effect> effects;

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public Player setOwner(Player newOwner){
        Player originalOwner = owner;
        this.owner = newOwner;
        return originalOwner;
    }

    @Override
    public Effect activeEffect(int num, Player[] targets){
        return effects.get(num);
    }
}
