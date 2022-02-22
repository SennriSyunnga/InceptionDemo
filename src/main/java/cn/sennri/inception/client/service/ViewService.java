package cn.sennri.inception.client.service;

public interface ViewService {

    /**
     * 由服务器推送初始化本地视图
     */
    void initialize();


    /**
     * 根据viewUpdate当前可视内容更改本地视图
     * 不用推送全部视图，而是推送一些事件
     */
    void update();


    /**
     * 本地校验效果发动条件；
     * 发动卡片效果，并向服务器进行结算请求
     */
    void active();
}
