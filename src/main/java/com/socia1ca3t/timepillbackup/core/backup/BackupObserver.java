package com.socia1ca3t.timepillbackup.core.backup;

import com.socia1ca3t.timepillbackup.pojo.vo.BackupProgressVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;
import java.util.function.BiConsumer;


public class BackupObserver implements Observer {

    private static final Logger logger = LoggerFactory.getLogger(BackupObserver.class);

    protected BiConsumer<Observable, BackupProgressVO> consumer;

    public BackupObserver() {
    }

    public BackupObserver(BiConsumer<Observable, BackupProgressVO> consumer) {

        this.consumer = consumer;
    }

    public void setConsumer(BiConsumer<Observable, BackupProgressVO> consumer) {

        this.consumer = consumer;
    }

    @Override
    public void update(Observable observable, Object arg) {

        if (consumer != null && arg instanceof BackupProgressVO) {

            if (logger.isDebugEnabled()) {
                logger.info("观察者收到通知：{}", arg);
            }

            consumer.accept(observable, (BackupProgressVO) arg);
        }
    }

}
