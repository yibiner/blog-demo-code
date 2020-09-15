package com.neo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn({"threadPoolHolder"})
public final class MessageService implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;

    @Override
    public void run(String... args) {
//        LOG.info("CAPACITY {}", Integer.toBinaryString(CAPACITY));
//        LOG.info("RUNNING {}", Integer.toBinaryString(RUNNING));
//        LOG.info("SHUTDOWN {}", Integer.toBinaryString(SHUTDOWN));
//        LOG.info("STOP {}", Integer.toBinaryString(STOP));
//        LOG.info("TIDYING {}", Integer.toBinaryString(TIDYING));
//        LOG.info("TERMINATED {}", Integer.toBinaryString(TERMINATED));
        //i的最大值为任务数
        for (int i = 0; i < 50; i++) {
            int finalI = i;
            ThreadPoolHolder.execute(() -> {
                LOG.info("running {} ActiveCount: {} PoolSize:{} TaskCount:{} Queue size:{}", finalI, ThreadPoolHolder.getExecutor().getActiveCount(),
                        ThreadPoolHolder.getExecutor().getPoolSize(), ThreadPoolHolder.getExecutor().getTaskCount(), ThreadPoolHolder.getExecutor().getQueue().size());

                try {
                    //模拟业务消耗时间
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        LOG.info("500ms ActiveCount: {} PoolSize:{} TaskCount:{} Queue size:{}", ThreadPoolHolder.getExecutor().getActiveCount(),
//                ThreadPoolHolder.getExecutor().getPoolSize(), ThreadPoolHolder.getExecutor().getTaskCount(), ThreadPoolHolder.getExecutor().getQueue().size());
//
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        LOG.info("2s ActiveCount: {} PoolSize:{} TaskCount:{} Queue size:{}", ThreadPoolHolder.getExecutor().getActiveCount(),
//                ThreadPoolHolder.getExecutor().getPoolSize(), ThreadPoolHolder.getExecutor().getTaskCount(), ThreadPoolHolder.getExecutor().getQueue().size());
    }
}
