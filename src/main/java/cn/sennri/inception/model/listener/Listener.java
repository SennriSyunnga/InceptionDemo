package cn.sennri.inception.model.listener;

import java.util.concurrent.CountDownLatch;

public class Listener<T> {
    private final CountDownLatch barrier = new CountDownLatch(1);

    private volatile T value;

    /**
     * 该方法应该只能被调用一次
     * @return
     * @throws InterruptedException
     */
    public T getBlocking() throws InterruptedException {
        barrier.await();
        return value;
    }

    public void setBlocking(T v) {
        this.value = v;
        barrier.countDown();
    }

}
