package com.socia1ca3t.timepillbackup.core.progress;

import com.socia1ca3t.timepillbackup.core.backup.BackupObservable;
import com.socia1ca3t.timepillbackup.core.backup.BackupObserver;
import com.socia1ca3t.timepillbackup.pojo.dto.BackupInfo;
import com.socia1ca3t.timepillbackup.pojo.vo.BackupProgressVO;

public class ProgressMonitor {

    public enum State {

        READY,
        DOWNLOADING,
        COMPRESSING,
        FINISHED,
        EXCEPTION
    }


    private final BackupInfo backupInfo;
    private final BackupObservable backupObservable;

    private volatile State state;
    private volatile long restTaskNum;
    private volatile Exception exception;


    public ProgressMonitor(BackupInfo info) {

        this.state = State.READY;
        this.backupInfo = info;
        this.backupObservable = new BackupObservable(info);

        backupObservable.setState(new BackupProgressVO(State.READY));
    }


    public void updateState(State state) {

        this.state = state;
        backupObservable.setState(new BackupProgressVO(state));
    }

    public void updateRestTaskNum(long restTaskNum) {

        this.restTaskNum = restTaskNum;
        this.state = State.DOWNLOADING;

        backupObservable.setState(new BackupProgressVO(state, restTaskNum));
    }

    public void endWithException(Exception e) {

        this.state = State.EXCEPTION;
        exception = e;

        backupObservable.setState(new BackupProgressVO(State.EXCEPTION, e.getMessage()));
    }

    public void addObserver(BackupObserver observer) {

        backupObservable.addObserver(observer);
    }


    public State getState() {
        return state;
    }

    public BackupInfo getBackupInfo() {
        return backupInfo;
    }

    public long getRestTaskNum() {
        return restTaskNum;
    }

    public Exception getException() {
        return exception;
    }

}
