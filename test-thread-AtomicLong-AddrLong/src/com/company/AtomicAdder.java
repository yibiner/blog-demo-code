package com.company;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class AtomicAdder {

    static volatile int counterVolatile = 0;

    public static void main(String[] args) throws InterruptedException {
        // 提交任务数
        long maxTaskCount = 100000;
        // 线程池工作线程数量
        int workerSize = 1;
        // 测试次数
        int testCount = 50;
        // 保存每次测试结果
        int[] recData = new int[testCount];

        AtomicLong counterAtomic = new AtomicLong(0);
        LongAdder counterAddr = new LongAdder();

        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(3000);
        RejectedExecutionHandler handler = new BlockRejectedExecutionHandler();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(workerSize, workerSize, 1, TimeUnit.SECONDS, workQueue, handler);

        for (int i = 0; i < testCount; i++) {
            long start = System.currentTimeMillis();
            for (int j = 0; j < maxTaskCount; j++) {
//                executor.submit(new TaskAtomic(counterAtomic));
//                executor.submit(new TaskVolatile());
                executor.submit(new TaskAdder(counterAddr));
            }

            // 记录多线程完成计数的时间点。可以比较两种方式下获取结果时间是否一致，因为 getCompletedTaskCount 根据说明返回值只是 approximation
            // 1、使用线程池完成任务数作为结束时间点
            while (executor.getCompletedTaskCount() < maxTaskCount * (i + 1)) ;

            // 2、根据获取累加值是否为设定值
//            while (counterAtomic.get() < maxTaskCount) ;
//            while (counterAddr.sum() < maxTaskCount) ;

            long end = System.currentTimeMillis();
//            System.out.println(counterAtomic.get());
//            System.out.println(counterVolatile);
//            System.out.println(counterAddr.sum());

            // 复位数据
            Thread.sleep(100);
            recData[i] = (int) (end - start);
            AtomicAdder.counterVolatile = 0;
            counterAtomic.set(0);
            counterAddr.reset();
        }
        System.out.println(Arrays.toString(recData));
        Arrays.sort(recData);
        long sumData = 0;
        // 取平均值时去掉了最大最小值
        for (int i = 1; i < recData.length - 1; i++) {
            sumData = sumData + recData[i];
        }
        System.out.println("平均值 " + sumData / (recData.length - 2));
        executor.shutdownNow();
    }

    static class TaskAtomic implements Runnable {

        private final AtomicLong counter;

        public TaskAtomic(AtomicLong counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            counter.incrementAndGet();
        }
    }

    static class TaskAdder implements Runnable {

        private final LongAdder counter;

        public TaskAdder(LongAdder counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            counter.increment();
        }
    }

    static class TaskVolatile implements Runnable {

        public TaskVolatile() {
        }

        @Override
        public void run() {
            AtomicAdder.counterVolatile++;
        }
    }
}
