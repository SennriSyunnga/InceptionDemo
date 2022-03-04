package cn.sennri.inception.card;

import cn.sennri.inception.Effect;
import cn.sennri.inception.server.Game;

import java.util.List;

public class Shoot extends AbcCard implements IShoot{


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
}
