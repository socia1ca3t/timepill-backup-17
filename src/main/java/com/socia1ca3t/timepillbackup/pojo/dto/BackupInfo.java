package com.socia1ca3t.timepillbackup.pojo.dto;

import com.socia1ca3t.timepillbackup.core.Backuper;

public class BackupInfo {

    private final String username;

    private final Backuper.Type type;

    private final int prepareCode;

    private Long prepareLogId;


    public BackupInfo(String username, Backuper.Type type, int prepareCode) {
        this.username = username;
        this.type = type;
        this.prepareCode = prepareCode;
    }

    public String getUsername() {
        return username;
    }

    public Backuper.Type getType() {
        return type;
    }

    public Long getPrepareLogId() {
        return prepareLogId;
    }

    public void setPrepareLogId(Long prepareLogId) {
        this.prepareLogId = prepareLogId;
    }

    public int getPrepareCode() {
        return prepareCode;
    }

}
