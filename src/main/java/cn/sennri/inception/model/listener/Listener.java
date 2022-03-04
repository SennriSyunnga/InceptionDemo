package cn.sennri.inception.model.listener;

import java.util.concurrent.CountDownLatch;

public class Listener<T> {
    private final CountDownLatch barrier = new CountDownLatch(1);

    private volatile T value;

    /**
     * 该方法在设计上只有第一次调用需要等待；
     * 不支持多次设置。
     * @return
     * @throws InterruptedException
     */
    public T getBlocking() throws InterruptedException {
        // 可能有同步问题吗？ 比如判断和返回value并非原子性的。
        if(value != null){
            return value;
        }
        barrier.await();
        return value;
    }

    /**
     * 该方法用于解除Blocking状态
     * @param v
     */
    public void setBlocking(T v) {
        this.value = v;
        barrier.countDown();
    }

}
