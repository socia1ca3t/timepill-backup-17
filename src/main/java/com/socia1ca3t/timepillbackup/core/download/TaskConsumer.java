package com.socia1ca3t.timepillbackup.core.download;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 图片下载任务消费者
 */
public class TaskConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TaskConsumer.class);

    // 延迟任务队列，保存生产者提交的任务
    private static final BlockingQueue<TaskWrapper> queue = new DelayQueue<>();

    // 下载图片的线程池
    private static final ThreadPoolTaskExecutor threadPool = taskExecutor();

    // 单例模式
    private static volatile TaskConsumer instance;


    private TaskConsumer() {

        Runnable consume = () -> {

            while (true) {

                try {
                    TaskWrapper task = queue.take();
                    if (task != null) {

                        logger.info("{},将任务提交到线程池...", task.hashCode());
                        threadPool.submit(() -> runTaskFunc.accept(task));
                    }

                } catch (Exception e) {
                    logger.error("从队列中获取待执行的任务时异常", e);
                }
            }
        };
        new Thread(consume).start();
    }

    private final Consumer<TaskWrapper> runTaskFunc = (task) -> {

        try {
            task.run();
            logger.info("{},任务执行完毕...", task.hashCode());
        } catch (Exception e) {

            logger.error("{}队列任务执行异常，设置延迟时间，再加入待执行任务队列", task.hashCode(), e);

            task.setDelay(1, TimeUnit.MINUTES);
            addTask(task);
        }
    };


    public void addTask(TaskWrapper task) {


        try {
            queue.put(task);
        } catch (InterruptedException e) {

            logger.error("将任务添加至待执行的任务时异常", e);
            throw new RuntimeException(e);
        }
    }


    public static TaskConsumer getInstance() {

        if (instance == null) {

            synchronized (TaskConsumer.class) {
                if (instance == null) {
                    instance = new TaskConsumer();
                }
            }
        }
        return instance;
    }


    public void stopConsumers() {
        threadPool.shutdown();
    }


    private static ThreadPoolTaskExecutor taskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3); // 设置核心线程数
        executor.setMaxPoolSize(6); // 设置最大线程数
        executor.setQueueCapacity(1000); // 设置队列容量
        executor.setThreadNamePrefix("Timepill-API-Thread-"); // 设置线程名称前缀
        executor.initialize(); // 初始化线程池
        return executor;
    }
}
