package cn.sennri.inception.server;

/**
 * 服务器内部的监听，一旦游戏结束就触发对应的消息推送
 */
public interface GameFinishedListener {

    void onHostSuccess();

    void onHostFailure();
}
