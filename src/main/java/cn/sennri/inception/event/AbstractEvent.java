package cn.sennri.inception.event;

/**
 * 用于使用type字段实现多态反序列化实现
 */
public abstract class AbstractEvent implements Event{
    private final String type;
    public AbstractEvent(){
        this.type = this.getClass().getSimpleName();
    }

    public String getType() {
        return type;
    }
}
