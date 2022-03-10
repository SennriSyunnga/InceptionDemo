package cn.sennri.inception.message;

import cn.sennri.inception.client.view.FieldView;

/**
 * 服务端发送游戏开始信息给游戏端
 * 此时需要初始化客户端的fieldView
 */
public class ServerStartGameMessage extends AbstractMessage{
    FieldView filedView;

    public FieldView getFiledView() {
        return filedView;
    }
}
