package com.socia1ca3t.timepillbackup.core.download;

import com.socia1ca3t.timepillbackup.core.ImgDownloader;
import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

@Slf4j
public class ImgSyncDownloader implements ImgDownloader {


    private List<Supplier<CountDownLatch>> downloadList;
    private ProgressMonitor monitor;


    @Override
    public void download() {

        if (downloadList == null || downloadList.isEmpty()) {
            return;
        }

        List<CountDownLatch> latchList = downloadList.stream().map(Supplier::get).toList();
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

    @Override
    public void setDownloadList(List<Supplier<CountDownLatch>> downloadList) {
        this.downloadList = downloadList;
    }

    @Override
    public void setMonitor(ProgressMonitor monitor) {
        this.monitor = monitor;
    }
}
