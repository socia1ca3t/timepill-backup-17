package com.socia1ca3t.timepillbackup.core.backup;

import com.socia1ca3t.timepillbackup.core.Backuper;
import com.socia1ca3t.timepillbackup.core.ImgDownloader;
import com.socia1ca3t.timepillbackup.core.download.ImgDownloaderClient;
import com.socia1ca3t.timepillbackup.core.path.ImgPathProduceForBackup;
import com.socia1ca3t.timepillbackup.core.path.ImgTagSrcPathSetterForHtml;
import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor;
import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor.State;
import com.socia1ca3t.timepillbackup.pojo.dto.*;
import com.socia1ca3t.timepillbackup.service.CurrentUserTimepillApiService;
import com.socia1ca3t.timepillbackup.util.BackupUtil;
import com.socia1ca3t.timepillbackup.util.CompressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.socia1ca3t.timepillbackup.core.ImgPathProducer.DIARY_IMG_FOLDER_NAME;


public abstract class AbstractHTMLBackuper implements Backuper {


    private static final Logger logger = LoggerFactory.getLogger(AbstractHTMLBackuper.class);
    protected UserDTO userInfo;
    protected final ImgTagSrcPathSetterForHtml imgPathSetter = new ImgTagSrcPathSetterForHtml(new ImgPathProduceForBackup());
    private final CurrentUserTimepillApiService currentUserTimepillApiService;
    private final ProgressMonitor monitor;

    AbstractHTMLBackuper(UserDTO userInfo, BackupInfo info, CurrentUserTimepillApiService currentUserTimepillApiService) {

        this.monitor = new ProgressMonitor(info);
        this.currentUserTimepillApiService = currentUserTimepillApiService;

        UserDTO copiedUserInfo = new UserDTO();
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

            // 获得所有需要下载的图片集合，并异步下载
            List<ImgDownloadInfo> imgWaitforDownloadList = getAllImageInfoWaitForDownload();
            CompletableFuture<Void> downloadAllImgTask = asyncDownloadAllImg(imgWaitforDownloadList, monitor);

            // 渲染HTML文件，以及生成通用文件
            CompletableFuture<File> generateFilesTask = CompletableFuture.supplyAsync(this::generateFiles);

            // 等待任务1和任务2都完成
            if (downloadAllImgTask != null) {
                downloadAllImgTask.join();
            }
            generateFilesTask.join();

            // 检查下载的日记图片的数量是否正确
            checkDiariesImgNumberAfterDownload(imgWaitforDownloadList, getGenerateFilesPath());

            monitor.updateState(State.COMPRESSING);
            compressFiles(generateFilesTask.get(), targetZipFile, userInfo.getEmail());

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

    private static CompletableFuture<Void> asyncDownloadAllImg(List<ImgDownloadInfo> imgWaitforDownloadList, ProgressMonitor monitor) {

        if (CollectionUtils.isEmpty(imgWaitforDownloadList)) {
            return null;
        }
        // 下载所有图片
        return CompletableFuture.runAsync(() -> ImgDownloaderClient.createSyncMode(imgWaitforDownloadList).monitor(monitor).download());
    }

    private static void checkDiariesImgNumberAfterDownload(List<ImgDownloadInfo> imgWaitforDownloadList, String fileBasePath) {

        if (CollectionUtils.isEmpty(imgWaitforDownloadList)) {
            return;
        }

        long allDiariesImgNum = imgWaitforDownloadList.stream().filter(imgInfo -> imgInfo.imgType() == ImgDownloader.ImgType.DIARY_IMG).count();
        if (allDiariesImgNum == 0) {
            return;
        }

        File baseFolder = new File(fileBasePath);
        long downloadDiariesImgNum = getAllDiariesImgNumInFolder(baseFolder);

        String logInfo = String.format("应下载日记图片的数量%d，实际下载日记图片的数量%d", allDiariesImgNum, downloadDiariesImgNum);
        logger.info(logInfo);

        if  (downloadDiariesImgNum != allDiariesImgNum) {
            throw new RuntimeException(logInfo);
        }
    }


    private static long getAllDiariesImgNumInFolder(File file) {

        if (file == null || file.isFile()) {
            return 0;
        }

        if (DIARY_IMG_FOLDER_NAME.equals(file.getName())) {

            return file.listFiles().length;
        } else {

            long allNum = 0;
            for (File inFile : file.listFiles()) {

                allNum += getAllDiariesImgNumInFolder(inFile);
            }
            return allNum;
        }
    }
    protected static void compressFiles(File sourceFile, File targetZipFile, String pwd) throws IOException {

        logger.info(targetZipFile.getName() + "文件准备完毕，开始压缩...");

        BackupUtil.makeDirs(targetZipFile);
        targetZipFile.createNewFile();
        try {
            CompressUtil.zipWithPassword(sourceFile, targetZipFile, pwd);

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

    protected List<NotebookDTO> getAllNotebooks () {

        return currentUserTimepillApiService.getCachableNotebookList();
    }

    protected List<DiaryDTO> getAllDiaries (int notebookId) {

        return currentUserTimepillApiService.getCachableDiaryList(notebookId);
    }

    @Override
    public ProgressMonitor getProgressMonitor() {
        return monitor;
    }
}
