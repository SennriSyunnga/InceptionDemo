package cn.sennri.inception.message;

public class ErrorMessage extends AbstractMessage{
    private final String content;
    public ErrorMessage(String content){
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
