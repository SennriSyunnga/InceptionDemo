package cn.sennri.inception.message;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 该消息用于在多方中传输卡片转移消息
 */
@NoArgsConstructor
@AllArgsConstructor
public class PrivateTransferCardMessage extends AbstractMessage{

    private int[] cards;

}
