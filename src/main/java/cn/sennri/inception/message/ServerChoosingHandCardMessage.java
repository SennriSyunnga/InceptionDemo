package cn.sennri.inception.message;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ServerChoosingHandCardMessage extends AbstractMessage{
    /**
     * 选出几张牌
     */
    Integer chooseNum;

    public Integer getChooseNum() {
        return chooseNum;
    }
}
