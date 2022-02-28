package cn.sennri.inception.model.listener;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Listener<T> {
    private CyclicBarrier barrier = new CyclicBarrier(2);

    private volatile T value;

    synchronized public T getBlocking() throws BrokenBarrierException, InterruptedException {
        barrier.await();
        return value;
    }

    synchronized public void setBlocking(T v) throws BrokenBarrierException, InterruptedException {
        this.value = v;
        barrier.await();
    }

}
