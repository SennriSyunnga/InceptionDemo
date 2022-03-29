package cn.sennri.inception.message;

import lombok.Getter;
import lombok.Setter;

/**
 * client发送给Server的revive消息。
 */
@Getter
@Setter
public class ReviveMessage extends AbstractMessage{
    int targetNum;
    /**
     * 丢弃的手牌
     */
    int[] costCardNum;
}
