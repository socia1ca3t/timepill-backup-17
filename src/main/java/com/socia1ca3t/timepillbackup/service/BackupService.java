package com.socia1ca3t.timepillbackup.service;

import com.socia1ca3t.timepillbackup.core.Backuper;
import com.socia1ca3t.timepillbackup.core.backup.AllNotebookHTMLBackuper;
import com.socia1ca3t.timepillbackup.core.backup.SingleNotebookHTMLBackuper;
import com.socia1ca3t.timepillbackup.pojo.dto.NotebookDTO;
import com.socia1ca3t.timepillbackup.pojo.dto.UserDTO;
import com.socia1ca3t.timepillbackup.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor.State.EXCEPTION;


@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class BackupService {

    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);


    public ResponseEntity<StreamingResponseBody> backupSingleNotebookToHTML(UserDTO user, String notebookId, RestTemplate userBasicAuthRestTemplate) {


        List<NotebookDTO> list = new CurrentUserTimepillApiService(userBasicAuthRestTemplate).getCachableNotebookList();

        NotebookDTO notebook = TimepillUtil.getNotebookById(list, Integer.parseInt(notebookId));
        Backuper backuper = new SingleNotebookHTMLBackuper(notebook, user, userBasicAuthRestTemplate);

        return backupToHTML(notebookId, backuper);
    }

    public ResponseEntity<StreamingResponseBody> backupSomeNotebookToHTML(UserDTO user, List<Integer> notebookIds, RestTemplate userBasicAuthRestTemplate) {


        List<NotebookDTO> allNotebookList = new CurrentUserTimepillApiService(userBasicAuthRestTemplate).getCachableNotebookList();

        List<NotebookDTO> someNotebookList = new ArrayList<>();
        for (Integer notebookId : notebookIds) {
            someNotebookList.add(TimepillUtil.getNotebookById(allNotebookList, notebookId));
        }

        Backuper backuper = new AllNotebookHTMLBackuper(user, someNotebookList, userBasicAuthRestTemplate);

        return backupToHTML(String.valueOf(user.getId()), backuper);
    }

    public ResponseEntity<StreamingResponseBody> backupAllNotebookToHTML(UserDTO user, RestTemplate userBasicAuthRestTemplate) {

        List<NotebookDTO> allNotebookList = new CurrentUserTimepillApiService(userBasicAuthRestTemplate).getCachableNotebookList();
        Backuper backuper = new AllNotebookHTMLBackuper(user, allNotebookList, userBasicAuthRestTemplate);

        return backupToHTML(String.valueOf(user.getId()), backuper);
    }


    public ResponseEntity<StreamingResponseBody> backupToHTML(final String prepareCode,
                                                              final Backuper backuper) {


        if (!LockUtil.getInstance().tryLock(prepareCode)) {
            return getHTMLStreamingResponseEntity("wait_patiently_for_download");
        }

        try {

            Backuper lastBackuper = BackupCacheUtil.get("HTMLBackup", prepareCode);
            if (lastBackuper != null && lastBackuper.getProgressMonitor().getState() != EXCEPTION) {

                logger.info("{}文件正在准备中，直接跳转至任务进度观察界面", prepareCode);
                return getHTMLStreamingResponseEntity("show_download_progress",
                        "prepareCode", prepareCode,
                        "zipFileName", backuper.getBackupZipFileName());
            }


            logger.info("{}开始异步准备文件", backuper.getBackupZipFileName());

            final CompletableFuture<File> prepareFilefuture = CompletableFuture.supplyAsync(backuper::execute);


            if (prepareFilefuture.isDone()) {

                if (prepareFilefuture.isCompletedExceptionally()) {

                    return getHTMLStreamingResponseEntity("show_warn_msg_page", "msg", "文件准备异常，请重试。");
                }

                logger.info("{}文件准备完毕", backuper.getBackupZipFileName());

                File zipFile = prepareFilefuture.join();
                StreamingResponseBody responseStream = deleteFileAfterSendingToClient(zipFile);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + zipFile.getName())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .contentLength(zipFile.length())
                        .body(responseStream);

            } else {

                // 供SseEmitter getDownloadProgress使用
                BackupCacheUtil.put("HTMLBackup", prepareCode, backuper);

                return getHTMLStreamingResponseEntity("show_download_progress",
                        "prepareCode", prepareCode,
                        "zipFileName", backuper.getBackupZipFileName());
            }

        } catch (Exception e) {

            logger.error("{}下载备份文件异常", prepareCode, e);
            throw new RuntimeException("下载备份文件异常");

        } finally {
            LockUtil.getInstance().unlock(prepareCode);
        }


    }


    public StreamingResponseBody deleteFileAfterSendingToClient(File fileForSending) {


        return outputStream -> {

            try (InputStream inputStream = new FileInputStream(fileForSending)) {

                IOUtils.copy(inputStream, outputStream);
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
