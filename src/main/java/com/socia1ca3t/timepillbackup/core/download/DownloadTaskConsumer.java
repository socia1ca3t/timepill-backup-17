package com.socia1ca3t.timepillbackup.core.download;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 图片下载任务消费者
 */
public class DownloadTaskConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DownloadTaskConsumer.class);

    // 延迟任务队列，保存生产者提交的任务
    private static final BlockingQueue<DownloadTaskWrapper> queue = new DelayQueue<>();

    // 下载图片的线程池
    private static final ThreadPoolTaskExecutor threadPool = taskExecutor();

    // 单例模式
    private static volatile DownloadTaskConsumer instance;


    private DownloadTaskConsumer() {

        Runnable consume = () -> {

            while (true) {

                try {
                    DownloadTaskWrapper task = queue.take();
                    if (task != null) {

                        logger.info("{},将任务提交到线程池...", task.hashCode());
                        Future<?> future = threadPool.submit(() -> runTaskFunc.accept(task));

                        try {
                            future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            logger.error("线程池任务执行异常", e);
                            task.setDelay(1, TimeUnit.MINUTES);
                            addTask(task);
                        }

                    }

                } catch (Exception e) {
                    logger.error("从队列中获取待执行的任务时异常", e);
                }
            }
        };
        new Thread(consume).start();
    }

    private final Consumer<DownloadTaskWrapper> runTaskFunc = (task) -> {

        try {
            task.run();
            logger.info("{},任务执行完毕...", task.hashCode());
        } catch (Exception e) {

            logger.error("{}队列任务执行异常，设置延迟时间，再加入待执行任务队列", task.hashCode(), e);

            task.setDelay(1, TimeUnit.MINUTES);
            addTask(task);
        }
    };


    public void addTask(DownloadTaskWrapper task) {


        try {
            queue.put(task);
        } catch (InterruptedException e) {

            logger.error("将任务添加至待执行的任务时异常", e);
            throw new RuntimeException(e);
        }
    }


    public static DownloadTaskConsumer getInstance() {

        if (instance == null) {

            synchronized (DownloadTaskConsumer.class) {
                if (instance == null) {
                    instance = new DownloadTaskConsumer();
                }
            }
        }
        return instance;
    }


    public void stopConsumers() {
        threadPool.shutdown();
    }


    public static ThreadPoolTaskExecutor taskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3); // 设置核心线程数
        executor.setMaxPoolSize(6); // 设置最大线程数
        executor.setQueueCapacity(1000); // 设置队列容量
        executor.setThreadNamePrefix("Timepill-API-Thread-"); // 设置线程名称前缀
        executor.initialize(); // 初始化线程池
        return executor;
    }
}
