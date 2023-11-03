package com.socia1ca3t.timepillbackup.core.backup;

import com.socia1ca3t.timepillbackup.pojo.dto.BackupInfo;
import com.socia1ca3t.timepillbackup.pojo.vo.BackupProgressVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;


public class BackupObservable extends Observable {

    private static final Logger logger = LoggerFactory.getLogger(BackupObservable.class);

    private BackupProgressVO historyProgressVO = null;

    private BackupInfo backupInfo;


    public BackupObservable(BackupInfo backupInfo) {

        this.backupInfo = backupInfo;
    }

    public synchronized void setState(BackupProgressVO newestProgressVO) {

        if (historyProgressVO == null
                || historyProgressVO.getRestTaskNum() != newestProgressVO.getRestTaskNum()
                || !newestProgressVO.isDownloading()) {

            historyProgressVO = newestProgressVO;
            setChanged();
            notifyObservers(newestProgressVO);

        }

    }


    @Override
    public synchronized void addObserver(Observer o) {

        logger.info("增加观察者，当前任务进度为{}", historyProgressVO != null ? historyProgressVO.getState() : "无进度");

        super.addObserver(o);
        if (historyProgressVO != null) {

            // 若状态已经变更过，马上通知观察者，防止丢失状态的改变
            setChanged();
            notifyObservers(historyProgressVO);
        }
    }


    public BackupInfo getBackupInfo() {
        return backupInfo;
    }

    public void setBackupInfo(BackupInfo backupInfo) {
        this.backupInfo = backupInfo;
    }
}
