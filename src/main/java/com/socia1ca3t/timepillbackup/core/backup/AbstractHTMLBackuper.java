package com.socia1ca3t.timepillbackup.core.backup;

import com.socia1ca3t.timepillbackup.core.Backuper;
import com.socia1ca3t.timepillbackup.core.ImgPathConvertor;
import com.socia1ca3t.timepillbackup.core.convert.ImgPathConvertorForDownload;
import com.socia1ca3t.timepillbackup.core.download.ImgDownloaderBuilder;
import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor;
import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor.State;
import com.socia1ca3t.timepillbackup.pojo.dto.BackupInfo;
import com.socia1ca3t.timepillbackup.pojo.dto.Diary;
import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;
import com.socia1ca3t.timepillbackup.pojo.dto.UserInfo;
import com.socia1ca3t.timepillbackup.service.CurrentUserTimepillApiService;
import com.socia1ca3t.timepillbackup.util.BackupUtil;
import com.socia1ca3t.timepillbackup.util.CompressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;


public abstract class AbstractHTMLBackuper implements Backuper {


    private static final Logger logger = LoggerFactory.getLogger(AbstractHTMLBackuper.class);

    protected UserInfo userInfo;
    private final CurrentUserTimepillApiService currentUserTimepillApiService;
    protected final ImgDownloaderBuilder downloaderBuilder;
    protected final ProgressMonitor monitor;

    AbstractHTMLBackuper(BackupInfo info, CurrentUserTimepillApiService currentUserTimepillApiService) {

        this.monitor = new ProgressMonitor(info);
        this.downloaderBuilder = new ImgDownloaderBuilder(new ImgPathConvertorForDownload()).monitor(monitor);
        this.currentUserTimepillApiService = currentUserTimepillApiService;
    }


    @Override
    public File execute() {

        final File targetZipFile = new File(getBackupZipFilePath());
        if (targetZipFile.exists()) {

            monitor.updateState(State.FINISHED);
            return targetZipFile;
        }

        try {
            File sourceFile = generateFiles();
            monitor.updateState(State.COMPRESSING);

            compressFiles(sourceFile, targetZipFile);
            monitor.updateState(State.FINISHED);

        } catch (Exception e) {

            logger.error("准备备份文件异常", e);
            monitor.endWithException(e);

            return null;
        }


        return targetZipFile;
    }


    protected abstract File generateFiles() throws IOException;


    protected void compressFiles(File sourceFile, File targetZipFile) throws IOException {

        logger.info(targetZipFile.getName() + "文件准备完毕，开始压缩...");

        BackupUtil.makeDirs(targetZipFile);
        targetZipFile.createNewFile();
        try {
            CompressUtil.zipWithPassword(sourceFile, targetZipFile, userInfo.getEmail());

        } catch (Exception e) {

            if (targetZipFile.exists()) {
                // 压缩失败，就删除空文件
                targetZipFile.delete();
            }

            logger.error("带密码压缩文件夹异常", e);
            throw new RuntimeException(e);
        }

        logger.info(targetZipFile.getName() + "文件压缩完成...");
    }

    protected List<NoteBook> getAllNotebooks () {

        return currentUserTimepillApiService.getCachableNotebookList();
    }

    protected List<Diary> getAllDiaries (int notebookId) {

        return currentUserTimepillApiService.getCachableDiaryList(notebookId);
    }

    @Override
    public String getBackupZipFilePath() {

        return ImgPathConvertor.FILE_BASE_PATH + "zip/" + getBackupZipFileName();
    }

    @Override
    public ProgressMonitor getProgressMonitor() {

        return monitor;
    }
}
