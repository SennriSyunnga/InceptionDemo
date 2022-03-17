package cn.sennri.inception.message;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ErrorMessage extends AbstractMessage{
    private String content;
    public ErrorMessage(String content){
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
