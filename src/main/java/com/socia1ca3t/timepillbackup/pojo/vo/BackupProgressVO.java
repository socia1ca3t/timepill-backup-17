package com.socia1ca3t.timepillbackup.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor;

import java.io.Serializable;
import java.util.Date;

/**
 * final 可以保证成员变量的可见性
 */
@JsonSerialize
public class BackupProgressVO implements Serializable {

    private final Date produceDate = new Date();

    private final ProgressMonitor.State state;

    private final long restTaskNum;

    private final boolean isDownloading;

    private final String msg;


    public BackupProgressVO(ProgressMonitor.State state, long restTaskNum, String msg) {

        if (state == ProgressMonitor.State.DOWNLOADING) {

            isDownloading = true;
        } else {
            isDownloading = false;
        }

        this.state = state;
        this.restTaskNum = restTaskNum;
        this.msg = msg;
    }

    public BackupProgressVO(ProgressMonitor.State progressState) {


        this(progressState, 0, null);
    }

    public BackupProgressVO(ProgressMonitor.State progressState, String msg) {

        this(progressState, 0, msg);
    }

    public BackupProgressVO(ProgressMonitor.State progressState, long restTaskNum) {

        this(progressState, restTaskNum, null);
    }

    public Date getProduceDate() {
        return produceDate;
    }

    public ProgressMonitor.State getState() {
        return state;
    }

    public long getRestTaskNum() {
        return restTaskNum;
    }


    public boolean isDownloading() {
        return isDownloading;
    }


    public String getMsg() {
        return msg;
    }

}
