package com.socia1ca3t.timepillbackup.pojo.dto;

import com.socia1ca3t.timepillbackup.core.Backuper;
import lombok.Data;


@Data
public class BackupInfo {

    private final String username;

    private final Backuper.Type type;

    private final int prepareCode;

    private Long prepareLogId;

}
