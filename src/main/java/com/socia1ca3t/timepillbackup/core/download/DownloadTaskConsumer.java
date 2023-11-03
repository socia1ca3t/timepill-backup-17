package com.socia1ca3t.timepillbackup.core.download;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(3);

    // 单例模式
    private static volatile DownloadTaskConsumer instance;


    private DownloadTaskConsumer() {

        Runnable consume = () -> {

            while (true) {

                try {
                    DownloadTaskWrapper task = queue.take();
                    if (task != null) {
                        threadPool.submit(() -> runTaskFunc.accept(task));
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
            logger.info("{}下载任务执行成功", task.hashCode());

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


}
