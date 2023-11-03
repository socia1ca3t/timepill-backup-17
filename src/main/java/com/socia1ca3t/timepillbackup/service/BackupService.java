package com.socia1ca3t.timepillbackup.service;

import com.socia1ca3t.timepillbackup.core.Backuper;
import com.socia1ca3t.timepillbackup.core.backup.AllNotebookHTMLBackuper;
import com.socia1ca3t.timepillbackup.core.backup.BackupObserver;
import com.socia1ca3t.timepillbackup.core.backup.SingleNotebookHTMLBackuper;
import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor;
import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;
import com.socia1ca3t.timepillbackup.pojo.dto.UserInfo;
import com.socia1ca3t.timepillbackup.pojo.entity.PrepareFilesLog;
import com.socia1ca3t.timepillbackup.pojo.vo.BackupProgressVO;
import com.socia1ca3t.timepillbackup.repository.PrepareFilesLogRepository;
import com.socia1ca3t.timepillbackup.util.*;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;


@Service
public class BackupService {

    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);


    @Autowired
    private LogService logService;

    @Autowired
    private PrepareFilesLogRepository prepareFilesRepo;


    public ResponseEntity<StreamingResponseBody> backupSingleNotebookToHTML(UserInfo user, String notebookId) {


        List<NoteBook> list = SpringContextUtil.getBean(CurrentUserTimepillApiService.class).getNotebookList(user.getEmail());

        NoteBook notebook = TimepillUtil.getNotebookById(list, Integer.parseInt(notebookId));
        Backuper backuper = new SingleNotebookHTMLBackuper(notebook, user);

        return backupToHTML(notebookId, backuper, Backuper.Type.SINGLE, user.getEmail());
    }

    public ResponseEntity<StreamingResponseBody> backupAllNotebookToHTML(UserInfo user) {

        Backuper backuper = new AllNotebookHTMLBackuper(user);

        return backupToHTML(String.valueOf(user.getId()), backuper, Backuper.Type.ALL, user.getEmail());
    }


    public ResponseEntity<StreamingResponseBody> backupToHTML(final String prepareCode,
                                                              final Backuper backuper,
                                                              final Backuper.Type type,
                                                              final String username) {


        if (!LockUtil.getInstance().tryLock(prepareCode)) {
            return getHTMLStreamingResponseEntity("wait_patiently_for_download");
        }

        try {

            Long prepareLogId = getPrepareIdIfBackuping(prepareCode);
            if (prepareLogId != null) {

                logger.info("{}文件正在准备中，直接跳转至任务进度观察界面", prepareCode);
                return getHTMLStreamingResponseEntity("show_download_progress",
                        "prepareCode", prepareCode,
                        "logId", String.valueOf(prepareLogId),
                        "zipFileName", backuper.getBackupZipFileName());
            }

            final Date downloadStartDate = new Date();
            final PrepareFilesLog prepareFilesLog = logService.insertPrepareFilesLog(username, downloadStartDate, null, type, prepareCode, backuper.getBackupZipFileName());

            logger.info("{}开始异步准备文件", backuper.getBackupZipFileName());

            final CompletableFuture<File> prepareFilefuture = CompletableFuture.supplyAsync(backuper::execute);

            if (prepareFilefuture.isDone()) {

                if (prepareFilefuture.isCompletedExceptionally()) {

                    return getHTMLStreamingResponseEntity("show_warn_msg_page", "msg", "文件准备异常，请重试。");
                }

                logService.updateCompeleDateOfPrepareFilesLog(prepareFilesLog, new Date());
                logger.info("{}文件准备完毕，用时{}分", backuper.getBackupZipFileName(), prepareFilesLog.getMinutesSpend());

                File zipFile = prepareFilefuture.join();
                StreamingResponseBody responseStream = deleteFileAfterSendingToClient(zipFile, zipFile);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + zipFile.getName())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .contentLength(zipFile.length())
                        .body(responseStream);

            } else {

                // 先清除历史任务的缓存，再创建新的，供SseEmitter getDownloadProgress使用
                CacheUtil.evictIfPresent("HTMLBackup", prepareCode);
                CacheUtil.put("HTMLBackup", prepareCode, backuper);


                backuper.getProgressMonitor().addObserver(getExceptionObserver(backuper, prepareFilesLog));
                backuper.getProgressMonitor().addObserver(getFinishedObserver(backuper, prepareFilesLog));


                backuper.getProgressMonitor().getBackupInfo().setPrepareLogId(prepareFilesLog.getId());

                return getHTMLStreamingResponseEntity("show_download_progress",
                        "prepareCode", prepareCode,
                        "logId", String.valueOf(prepareFilesLog.getId()),
                        "zipFileName", backuper.getBackupZipFileName());
            }

        } catch (Exception e) {

            logger.error("{}下载备份文件异常", prepareCode, e);
            throw new RuntimeException("下载备份文件异常");

        } finally {
            LockUtil.getInstance().unlock(prepareCode);
        }


    }


    private Long getPrepareIdIfBackuping(String prepareCode) {

        List<PrepareFilesLog> list = prepareFilesRepo.findByCompleteDateAndExceptionDateIsNull(prepareCode);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0).getId();
    }


    /**
     * 备份文件任务异常状态的观察者
     *
     * @param backuper 文件备份任务
     * @param log      此次备份文件的log对象
     * @return 文件准备异常的观察者
     */
    private BackupObserver getExceptionObserver(Backuper backuper, PrepareFilesLog log) {

        BiConsumer<Observable, BackupProgressVO> consumer = (observable, downloadProgressVO) -> {

            if (downloadProgressVO.getState() == ProgressMonitor.State.EXCEPTION) {

                logger.info("{}文件准备异常:{}", backuper.getBackupZipFileName(), downloadProgressVO.getMsg());

                logService.updateExceptionDateOfPrepareFilesLog(log, new Date(), downloadProgressVO.getMsg());
            }

        };


        return new BackupObserver(consumer);
    }


    /**
     * 备份文件任务结束状态的观察者
     *
     * @param backuper 文件备份任务
     * @param log      此次备份文件的log对象
     * @return 文件准备异常的观察者
     */
    private BackupObserver getFinishedObserver(Backuper backuper, PrepareFilesLog log) {

        BiConsumer<Observable, BackupProgressVO> consumer = (observable, downloadProgressVO) -> {

            if (downloadProgressVO.getState() == ProgressMonitor.State.FINISHED) {

                logger.info("{}文件准备完毕", backuper.getBackupZipFileName());

                logService.updateCompeleDateOfPrepareFilesLog(log, new Date());
            }
        };

        return new BackupObserver(consumer);
    }


    public StreamingResponseBody deleteFileAfterSendingToClient(File fileWaitForDeleting, File fileForSending) {


        return outputStream -> {

            try (InputStream inputStream = new FileInputStream(fileForSending)) {

                IOUtils.copy(inputStream, outputStream);

                if (fileWaitForDeleting.delete()) {
                    logger.info("{}文件下载完成，删除文件成功", fileWaitForDeleting.getName());
                    logService.insertFileDeleteLog(true, fileWaitForDeleting.getAbsolutePath(), new Date(), new Date());

                } else {
                    logger.info("{}文件下载完成，删除文件失败", fileWaitForDeleting.getName());
                    logService.insertFileDeleteLog(false, fileWaitForDeleting.getAbsolutePath(), new Date(), null);
                }
            } catch (IOException e) {
                logger.error("响应文件异常，请重试", e);
            }
        };
    }


    public ResponseEntity<StreamingResponseBody> getHTMLStreamingResponseEntity(String templateName, String... keyValuePairs) {

        Map<String, String> map = HashMapGeneratorUtil.generate(keyValuePairs);
        String html = TemplateUtil.render2html(map, templateName);

        StreamingResponseBody stream = outputStream -> outputStream.write(html.getBytes(StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(stream);
    }

}
