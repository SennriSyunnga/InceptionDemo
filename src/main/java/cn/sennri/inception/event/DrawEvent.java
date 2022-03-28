package cn.sennri.inception.event;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DrawEvent extends AbstractEvent implements Event{
    public DrawEvent(){}
    /**
     * 玩家编号
     */
    int subject;

    /**
     * 抽卡量
     */
    int count;


    public int getCount() {
        return count;
    }

    public int getSubject() {
        return subject;
    }
}
