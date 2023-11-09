package com.socia1ca3t.timepillbackup.controller;

import com.socia1ca3t.timepillbackup.config.CurrentUser;
import com.socia1ca3t.timepillbackup.config.CurrentUserBasicAuthRestTemplate;
import com.socia1ca3t.timepillbackup.core.Backuper;
import com.socia1ca3t.timepillbackup.core.ImgPathProducer;
import com.socia1ca3t.timepillbackup.core.backup.BackupObservable;
import com.socia1ca3t.timepillbackup.core.backup.BackupObserver;
import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor;
import com.socia1ca3t.timepillbackup.pojo.dto.UserInfo;
import com.socia1ca3t.timepillbackup.pojo.vo.BackupProgressVO;
import com.socia1ca3t.timepillbackup.service.BackupService;
import com.socia1ca3t.timepillbackup.util.BackupCacheUtil;
import com.socia1ca3t.timepillbackup.util.DateUtil;
import jakarta.validation.constraints.NotBlank;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

@Controller
@RequestMapping("/download")
public class BackupController {

    private static final Logger logger = LoggerFactory.getLogger(BackupController.class);


    @Autowired
    private BackupService backupService;


    @RequestMapping(value = "/html/notebook/{notebookId}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<StreamingResponseBody> downloadSingleNotebookForHTML(@PathVariable @NotBlank String notebookId,
                                                                               @CurrentUser UserInfo user,
                                                                               @CurrentUserBasicAuthRestTemplate RestTemplate userBasicAuthRestTemplate) {

        return backupService.backupSingleNotebookToHTML(user, notebookId, userBasicAuthRestTemplate);
    }


    @RequestMapping(value = "/html/notebooks/{userId}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<StreamingResponseBody> downloadAllNotebookForHTML(@CurrentUser UserInfo user,
                                                                            @CurrentUserBasicAuthRestTemplate RestTemplate userBasicAuthRestTemplate) {

        return backupService.backupAllNotebookToHTML(user, userBasicAuthRestTemplate);
    }


    @GetMapping(value = "/progress/{prepareCode}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getDownloadProgress(@PathVariable String prepareCode) {


        SseEmitter sseEmitter = new SseEmitter(0L);

        logger.info("{}注册SSE回调观察者开始", prepareCode);

        BackupObserver observer = new BackupObserver();
        observer.setConsumer(getObserverConsumer(observer, prepareCode, sseEmitter));

        Backuper backuper = BackupCacheUtil.get("HTMLBackup", prepareCode);
        if (backuper != null) {

            backuper.getProgressMonitor().addObserver(observer);
            logger.info("{}注册SSE回调观察者完成", prepareCode);

        } else {

            logger.error("{}未获取到HTMLBackup备份器", prepareCode);
            BackupProgressVO progressVO = new BackupProgressVO(ProgressMonitor.State.EXCEPTION, "未获取到HTMLBackuper备份器");
            try {
                sseEmitter.send(SseEmitter.event().data(progressVO, MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                logger.error("响应sse数据异常", e);
            }
            sseEmitter.complete();
        }


        return sseEmitter;
    }

    private BiConsumer<Observable, BackupProgressVO> getObserverConsumer(BackupObserver observer, String prepareCode, SseEmitter sseEmitter) {


        AtomicReference<BackupProgressVO> historyProgressVORef = new AtomicReference<>();
        historyProgressVORef.set(new BackupProgressVO(null));

        return (observable, newestProgressVO) -> {

            /*
             * sseEmitter.send是同步方法，要避免阻塞
             * 处于 Downloading 下载的状态，每 2S 传输一次状态
             * 刚注册，及时传输状态
             * 处于非下载中的状态时，及时传输状态
             */

            synchronized (this) {

                if (historyProgressVORef.get().getState() == null
                        || !newestProgressVO.isDownloading()
                        || DateUtil.millisBetweenDate(historyProgressVORef.get().getProduceDate(),
                        newestProgressVO.getProduceDate()) >= 1000) {

                    BackupObservable backupObservable = (BackupObservable) observable;

                    logger.info("[{}]的{}任务，备份编号{}，备份码{}，当前状态：{}，剩余数量：{}",
                            backupObservable.getBackupInfo().getUsername(),
                            backupObservable.getBackupInfo().getType(),
                            backupObservable.getBackupInfo().getPrepareLogId(),
                            prepareCode,
                            newestProgressVO.getState(),
                            newestProgressVO.getRestTaskNum());

                    try {
                        sseEmitter.send(SseEmitter.event().data(newestProgressVO, MediaType.APPLICATION_JSON));

                    } catch (Exception e) {

                        logger.error("向前端响应下载进度错误，前端终止异常", e);

                        /*
                         * 若浏览器端在 complete 前关闭了页面，就会触发CloseNowException、CloseNowException等异常
                         * 及时结束并解除观察，防止持续异常
                         */
                        sseEmitter.complete();
                        observable.deleteObserver(observer);

                    }

                    historyProgressVORef.set(newestProgressVO);
                }
            }


            if (newestProgressVO.getState() == ProgressMonitor.State.FINISHED
                    || newestProgressVO.getState() == ProgressMonitor.State.EXCEPTION) {

                // 不能在这里清除缓存，会导致后面的get空指针，无法注册观察者，也就无法获取最新状态
                // CacheUtil.evictIfPresent("HTMLBackup", prepareCode);
                sseEmitter.complete();
                observable.deleteObserver(observer);
            }
        };
    }

    @GetMapping(value = "/backup/{zipFileName}")
    public ResponseEntity<StreamingResponseBody> downloadFile(
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            @PathVariable String zipFileName,
            @RequestParam("prepareCode") String prepareCode) throws IOException {


        // 清除此次任务的缓存
        BackupCacheUtil.evictIfPresent("HTMLBackup", prepareCode);

        File file = new File(ImgPathProducer.FILE_BASE_PATH + "zip/" + zipFileName);
        if (!file.exists()) {
            return backupService.getHTMLStreamingResponseEntity("show_warn_msg_page",
                    "msg", "文件已被删除，请重新下载");
        }

        logger.info("开始下载文件{}", zipFileName);

        Resource resource = new FileSystemResource(file);
        long contentLength = resource.contentLength();
        List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
        long start = getRangeStart(ranges, contentLength);
        long end = getRangeEnd(ranges, contentLength);
        ResourceRegion resourceRegion = new ResourceRegion(resource, start, end - start + 1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(resourceRegion.getCount());
        headers.set("Content-Range", "bytes " + start + "-" + end + "/" + contentLength);
        headers.set("Accept-Ranges", "bytes");
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());


        if (!ranges.isEmpty()) {

            if (end == contentLength - 1) {

                StreamingResponseBody responseStream = backupService.deleteFileAfterSendingToClient(file, resourceRegion.getResource().getFile());

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(responseStream);

            }

            StreamingResponseBody responseStream = outputStream -> {

                try (InputStream inputStream = resourceRegion.getResource().getInputStream()) {

                    IOUtils.copy(inputStream, outputStream);
                } catch (IOException e) {
                    logger.error("响应文件异常", e);
                }
            };

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(responseStream);
        } else {

            ResourceRegion fullResourceRegion = new ResourceRegion(resource, 0, contentLength);
            StreamingResponseBody responseStream = backupService.deleteFileAfterSendingToClient(file, fullResourceRegion.getResource().getFile());

            return ResponseEntity.status(HttpStatus.OK)
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(responseStream);
        }
    }

    private long getRangeStart(List<HttpRange> ranges, long contentLength) {

        if (ranges.isEmpty()) {
            return 0;
        }
        return ranges.get(0).getRangeStart(contentLength);
    }

    private long getRangeEnd(List<HttpRange> ranges, long contentLength) {

        if (ranges.isEmpty()) {
            return contentLength - 1;
        }
        return ranges.get(0).getRangeEnd(contentLength);
    }


}
