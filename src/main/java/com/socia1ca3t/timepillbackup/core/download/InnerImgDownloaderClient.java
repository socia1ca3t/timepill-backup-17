package com.socia1ca3t.timepillbackup.core.download;

import com.socia1ca3t.timepillbackup.core.ImgDownloader;
import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;


@Slf4j
class InnerImgDownloaderClient implements ImgDownloader {


    private List<Supplier<CountDownLatch>> downloadList;
    private final ProgressMonitor monitor;
    private final Mode mode;
    private boolean started = false;

    public InnerImgDownloaderClient(List<Supplier<CountDownLatch>> downloadList, Mode mode, ProgressMonitor monitor) {
        this.downloadList = downloadList;
        this.mode = mode;
        this.monitor = monitor;
    }

    @Override
    public void download() {

        if (downloadList == null || downloadList.isEmpty()) {
            return;
        }

        synchronized (this) {
            if (started) {
                throw new RuntimeException("任务已启动，请勿重复执行");
            }
            started = true;
        }

        if (mode == Mode.SYNC || mode == null) {

            log.info("开始同步执行下载任务...");
            doDownload();
        } else if (mode == Mode.ASYNC) {

            log.info("开始异步执行下载任务...");
            new Thread(this::doDownload).start();
        }
    }

    private void doDownload() {

        // 开始执行下载任务，并获得所有的 CountDownLatch
        List<CountDownLatch> latchList = downloadList.stream().map(Supplier::get).toList();
        downloadList = null;

        Optional<Long> restTaskNum;
        do {
            restTaskNum = latchList.stream().map(CountDownLatch::getCount).reduce(Long::sum);
            if (restTaskNum.isPresent() && monitor != null) {
                // 更新剩余任务的数量
                monitor.updateRestTaskNum(restTaskNum.get());
            }
        } while (restTaskNum.isPresent() && restTaskNum.get() != 0);

        log.info("下载任务执行完毕...");
    }

}
