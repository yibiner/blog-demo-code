package com.company;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class BlockRejectedExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!executor.isShutdown()) {
            try {

                //当队列无法offer，且当前线程池大小超过maximumPoolSize时，提交的Runnable就会被拒绝
                //调用队列的put阻塞当前线程
                executor.getQueue().put(r);
            } catch (InterruptedException e) {

            }
        }
    }
}
