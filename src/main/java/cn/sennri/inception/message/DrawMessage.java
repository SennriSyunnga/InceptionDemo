package cn.sennri.inception.message;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DrawMessage extends AbstractMessage{
    int[] cardIds;

    public int[] getCardIds() {
        return cardIds;
    }
}
