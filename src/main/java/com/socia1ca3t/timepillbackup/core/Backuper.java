package com.socia1ca3t.timepillbackup.core;


import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor;
import com.socia1ca3t.timepillbackup.pojo.dto.ImagesDownloadData;

import java.io.File;

public interface Backuper {

    enum Type {
        ALL,
        SINGLE
    }

    /**
     * 不同的下载类型，日记模板不同
     *
     * @return 日记模板的路径
     */
    String getDiaryTemplatePath();


    /**
     * 不同的下载类型，目标文件夹不同
     *
     * @return 目标文件夹的路径
     */
    String getGenerateFilesPath();


    String getBackupZipFileName();


    String getBackupZipFilePath();

    /**
     * 生成压缩的备份文件
     */
    File execute();

    ProgressMonitor getProgressMonitor();

    ImagesDownloadData getImagesDownloadData();
}
