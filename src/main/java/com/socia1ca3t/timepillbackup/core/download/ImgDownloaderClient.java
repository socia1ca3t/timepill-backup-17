package com.socia1ca3t.timepillbackup.core.download;

import com.socia1ca3t.timepillbackup.core.ImgDownloader;
import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor;
import com.socia1ca3t.timepillbackup.pojo.dto.ImgDownloadInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 图片的CDN服务器开启了图片防盗链，故需要下载图片，供前端展示时使用
 */
@Slf4j
public class ImgDownloaderClient {

    private final List<ImgDownloadInfo> imgDownloadInfos;
    private ProgressMonitor monitor;
    private final ImgDownloader.Mode mode;
    private boolean started = false;

    public static ImgDownloaderClient createSyncMode(List<ImgDownloadInfo> imgDownloadInfos) {

        return new ImgDownloaderClient(imgDownloadInfos, ImgDownloader.Mode.SYNC);
    }

    public static ImgDownloaderClient createAsyncMode(List<ImgDownloadInfo> imgDownloadInfos) {

        return new ImgDownloaderClient(imgDownloadInfos, ImgDownloader.Mode.ASYNC);
    }

    private ImgDownloaderClient(List<ImgDownloadInfo> imgDownloadInfos, ImgDownloader.Mode mode) {

        this.imgDownloadInfos = imgDownloadInfos;
        this.mode = mode;
    }

    public ImgDownloaderClient monitor(ProgressMonitor monitor) {

        this.monitor = monitor;
        return this;
    }

    public void download() {

        if (CollectionUtils.isEmpty(imgDownloadInfos)) {
            return;
        }

        synchronized (this) {
            if (started) {
                throw new RuntimeException("任务已启动，请勿重复执行");
            }
            started = true;
        }

        if (mode == ImgDownloader.Mode.SYNC) {

            log.info("开始同步执行下载任务...");
            doDownload();

        } else if (mode == ImgDownloader.Mode.ASYNC) {

            log.info("开始异步执行下载任务...");
            new Thread(this::doDownload).start();
        }
    }


    private void doDownload() {

        CountDownLatch latch = new CountDownLatch(imgDownloadInfos.size());
        imgDownloadInfos.stream().parallel().forEach(imgInfo-> {

            TaskConsumer.getInstance().addTask(new TaskWrapper(() -> {

                ImgRealDownloader.download(imgInfo.url(), imgInfo.absolutePath(), imgInfo.fileName());
                latch.countDown();
            }));
        });

        while(latch.getCount() != 0) {


            if (monitor != null) {
                // 更新剩余任务的数量
                monitor.updateRestTaskNum(latch.getCount());
            }
        }
        log.info("下载任务执行完毕...");
    }

}
