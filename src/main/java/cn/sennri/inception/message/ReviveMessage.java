package cn.sennri.inception.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviveMessage extends AbstractMessage{
    int targetNum;
    /**
     * 丢弃的手牌
     */
    int[] costCardNum;

}
