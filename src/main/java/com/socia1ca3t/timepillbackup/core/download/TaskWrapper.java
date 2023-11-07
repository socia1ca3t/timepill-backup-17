package com.socia1ca3t.timepillbackup.core.download;


import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class TaskWrapper implements Runnable, Delayed {

    private final Runnable task;

    private long delayTimeMillis = 0;

    public TaskWrapper(Runnable task) {
        this.task = task;
    }

    /**
     * 设置延迟时间
     */
    public void setDelay(long delayTime, TimeUnit timeUnit) {

        this.delayTimeMillis = System.currentTimeMillis() + timeUnit.toMillis(delayTime);
    }

    @Override
    public void run() {
        task.run();
    }

    /**
     * 队列剩余的延迟时间
     *
     * @param unit 调用程序指定的时间单位
     * @return duration 转换为 unit 指定的时间单位之后的时间
     */
    @Override
    public long getDelay(TimeUnit unit) {

        if (delayTimeMillis == 0) return 0;

        long delay = delayTimeMillis - System.currentTimeMillis();
        return unit.convert(delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {

        if (this == other) {
            return 0; // 同一个任务，延迟相同
        }

        long diff = this.getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);

        if (diff < 0) {
            return -1; // 当前任务的延迟小于其他任务，具有更高的优先级
        } else if (diff > 0) {
            return 1; // 当前任务的延迟大于其他任务，具有较低的优先级
        } else {
            return 0; // 延迟相同
        }
    }
}
