package cn.sennri.inception.event;

public class DrawEvent {
    /**
     * 抽卡量
     */
    int count;
    /**
     * 玩家编号
     */
    int subject;
    /**
     * 卡片在整个卡池中的编号。
     * 如果subject是自己，则cardUid非空，否则为空。
     */
    int[] cardUid;

    public int getCount() {
        return count;
    }

    public int getSubject() {
        return subject;
    }

    public int[] getCardUid() {
        return cardUid;
    }
}
