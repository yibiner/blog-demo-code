package com.neo.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;


@Component
public final class ThreadPoolHolder {
    private static ThreadPoolExecutor executor = null;
    private static ThreadPoolExecutor subExecutor;
    private static ScheduledExecutorService scheduledExecutor = null;
    @Value("${executorMaximumPoolSize:10}")
    private int executorMaximumPoolSize;
    @Value("${scheduleThreads:5}")
    private int scheduleThreads;
    @Value("${corePoolSize:4}")
    private int corePoolSize;
    @Value("${subCorePoolSize:4}")
    private int subCorePoolSize;

    private ThreadPoolHolder() {
    }

    public static void subExecute(Runnable command) {
        subExecutor.execute(command);
    }

    public static void execute(Runnable command) {
        executor.execute(command);
    }

    public static void schedule(Runnable command, long delay, TimeUnit unit) {
        scheduledExecutor.schedule(command, delay, unit);
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return scheduledExecutor.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    private static void close(ExecutorService service) {
        service.shutdown();
        try {

            if (!service.awaitTermination(2, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException ie) {

            service.shutdownNow();

            Thread.currentThread().interrupt();
        }
    }

    public static void close() {
        if (executor != null) {
            close(executor);
        }

        if (scheduledExecutor != null) {
            close(scheduledExecutor);
        }

        if (subExecutor != null) {
            close(subExecutor);
        }
    }


    public static ThreadPoolExecutor getExecutor() {
        return executor;
    }

    @PostConstruct
    public void init() {
        if (executor == null) {

            BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(20);

            RejectedExecutionHandler handler = new BlockRejectedExecutionHandler();

            executor = new ThreadPoolExecutor(corePoolSize, executorMaximumPoolSize, 1, TimeUnit.SECONDS, workQueue, handler);
//            executor.allowCoreThreadTimeOut(true);
        }
        if (subExecutor == null) {
            BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(20);

            RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
            subExecutor = new ThreadPoolExecutor(subCorePoolSize, executorMaximumPoolSize, 1, TimeUnit.SECONDS, workQueue, handler);
        }
        if (scheduledExecutor == null) {
            if (scheduleThreads > 0) {
                scheduledExecutor = Executors.newScheduledThreadPool(scheduleThreads);
            }
        }
    }

}

