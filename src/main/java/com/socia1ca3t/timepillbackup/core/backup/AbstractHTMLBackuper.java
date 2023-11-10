package com.socia1ca3t.timepillbackup.core.backup;

import com.socia1ca3t.timepillbackup.core.Backuper;
import com.socia1ca3t.timepillbackup.core.ImgPathProducer;
import com.socia1ca3t.timepillbackup.core.download.ImgDownloaderClient;
import com.socia1ca3t.timepillbackup.core.path.ImgPathProduceForBackup;
import com.socia1ca3t.timepillbackup.core.path.ImgTagSrcPathSetterForHtml;
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
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public abstract class AbstractHTMLBackuper implements Backuper {


    private static final Logger logger = LoggerFactory.getLogger(AbstractHTMLBackuper.class);
    protected UserInfo userInfo;
    protected final ImgTagSrcPathSetterForHtml imgPathSetter = new ImgTagSrcPathSetterForHtml(new ImgPathProduceForBackup());
    private final CurrentUserTimepillApiService currentUserTimepillApiService;
    private final ProgressMonitor monitor;

    AbstractHTMLBackuper(UserInfo userInfo, BackupInfo info, CurrentUserTimepillApiService currentUserTimepillApiService) {

        this.monitor = new ProgressMonitor(info);
        this.currentUserTimepillApiService = currentUserTimepillApiService;

        UserInfo copiedUserInfo = new UserInfo();
        BeanUtils.copyProperties(userInfo, copiedUserInfo);
        this.userInfo = copiedUserInfo;
    }


    @Override
    public File execute() {

        final File targetZipFile = new File(getBackupZipFilePath());
        if (targetZipFile.exists() && targetZipFile.length() != 0) {

            monitor.updateState(State.FINISHED);
            return targetZipFile;
        }

        try {
            // 初始化所有数据
            initData();

            // 下载所有图片
            CompletableFuture<Void> downloadAllImg = CompletableFuture.runAsync(() -> ImgDownloaderClient
                                                                            .createSyncMode(getAllImageDownloadInfo())
                                                                            .monitor(monitor).download());
            // 渲染HTML文件，以及生成通用文件
            CompletableFuture<File> generateFiles = CompletableFuture.supplyAsync(this::generateFiles);

            // 等待任务1和任务2都完成
            CompletableFuture.allOf(downloadAllImg, generateFiles).join();

            monitor.updateState(State.COMPRESSING);
            compressFiles(generateFiles.get(), targetZipFile);

            monitor.updateState(State.FINISHED);

        } catch (Exception e) {

            logger.error("准备备份文件异常{}", targetZipFile.getName(), e);
            monitor.endWithException(e);

            return null;
        }

        return targetZipFile;
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    protected abstract File generateFiles();


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

        return ImgPathProducer.FILE_BASE_PATH + "zip/" + getBackupZipFileName();
    }

    @Override
    public ProgressMonitor getProgressMonitor() {

        return monitor;
    }
}
