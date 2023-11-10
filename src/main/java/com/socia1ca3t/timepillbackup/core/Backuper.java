package com.socia1ca3t.timepillbackup.core;


import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor;
import com.socia1ca3t.timepillbackup.pojo.dto.ImgDownloadInfo;

import java.io.File;
import java.util.List;

public interface Backuper {

    enum Type {
        ALL,
        SINGLE
    }

    /**
     * 不同的下载类型，日记模板不同
     * @return 日记模板的路径
     */
    String getDiaryTemplatePath();


    /**
     * 所有文件准备完毕后，获取该文件夹的路径
     */
    String getGenerateFilesPath();


    /**
     * 所有需要备份的文件压缩后的文件名
     */
    String getBackupZipFileName();

    /**
     * 所有需要备份的文件压缩后的文件的路径
     */
    String getBackupZipFilePath();

    /**
     * 执行备份任务
     */
    File execute();

    ProgressMonitor getProgressMonitor();

    List<ImgDownloadInfo> getAllImageDownloadInfo();
}
