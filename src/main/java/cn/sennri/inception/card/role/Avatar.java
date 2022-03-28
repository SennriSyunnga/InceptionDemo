package cn.sennri.inception.card.role;

import cn.sennri.inception.AbcEffect;
import cn.sennri.inception.card.Card;
import cn.sennri.inception.event.Event;
import cn.sennri.inception.event.MoveEvent;
import cn.sennri.inception.player.Player;
import cn.sennri.inception.server.Game;

import java.util.Collections;
import java.util.List;

public class Avatar extends BaseRoleCard{
    public Avatar(Player owner, Game game) {
        super(owner, game);
        this.effects = Collections.singletonList(new DrawEffect(this));
    }

    static class DrawEffect extends AbcEffect {
        public DrawEffect(Card effectSource) {
            super(effectSource);
        }

        boolean canActive;

        @Override
        public void refresh() {
            super.refresh();
            canActive = true;
        }

        @Override
        public boolean isActivationLegal(Game game, Player source, int[] targets) {
            if (!canActive){
                return false;
            }
            if (isActivable(game)){
                return false;
            }
            List<Event> eventList = game.getEventList();
            for(Event event:eventList){
                if(event instanceof MoveEvent){
                    int subject = ((MoveEvent) event).getSubject();
                    if (subject != this.source.getOrder()){
                        continue;
                    }
                    boolean up = ((MoveEvent) event).getUp();
                    if (up){
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void takeEffect(Game game) {
            game.drawDeck(this.source, 2);
            // 生效后重置，这样一组连锁上只能发动一次，避免对同一个移动事件的重复识别
            refresh();
        }
    }
}
