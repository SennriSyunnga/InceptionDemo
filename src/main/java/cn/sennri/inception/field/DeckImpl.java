package cn.sennri.inception.field;

import cn.sennri.inception.Effect;
import cn.sennri.inception.card.Card;


import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class DeckImpl extends AbcDeck implements Deck {

    CountDownLatch hostWinCondition;

    final static int MAX_CAPABILITY = 70;

    public DeckImpl(){
        this.hostWinCondition = new CountDownLatch(MAX_CAPABILITY);
    }

    @Override
    public Card draw() {
        hostWinCondition.countDown();
        return deck.pollLast();
    }

    @Override
    public void abandon(int num, List<Card> graveyard, List<Card> exclusionZone) {
        for(int i = 0;i < num;i++){
            Card card = deck.pollLast();
            for (Effect e:card.getEffects()){
                // 传一个可变枚举数组？

                if (e.isActivable(new ArrayDeque<>())){
                    ArrayDeque<Effect> stack = new ArrayDeque<>();
                    stack.push(e);
                    // 等待响应

                    //响应结束
                    while(!stack.isEmpty()){
                        Effect effect = stack.pop();
                        if (!effect.isDeactivated()){
                            effect.takeEffect(stack, this, graveyard, exclusionZone, null, null);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void shuffle() {

    }

    @Override
    public boolean isEmpty() {
        return this.deck.isEmpty();
    }

    @Override
    public CountDownLatch getHostWinCondition() {
        return null;
    }
}
