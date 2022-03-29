package cn.sennri.inception.message;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ClientChosenCardMessage extends AbstractReplyMessage<int[]>{
    int[] cardIds;
    public int[] getCardIds(){return this.cardIds;}
}
