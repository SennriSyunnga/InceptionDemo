package cn.sennri.inception.message;

public class GameOverMessage extends AbstractMessage{
    public Boolean getHostWin() {
        return hostWin;
    }

    /**
     * 用于反序列化
     */
    public GameOverMessage(){

    }

    public GameOverMessage(boolean hostWin){
        super();
        this.hostWin = hostWin;
    }


    Boolean hostWin;
}
