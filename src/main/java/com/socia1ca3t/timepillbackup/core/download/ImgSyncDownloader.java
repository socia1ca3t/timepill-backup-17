package com.socia1ca3t.timepillbackup.core.download;

import com.socia1ca3t.timepillbackup.core.ImgDownloader;
import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

public class ImgSyncDownloader implements ImgDownloader {


    private List<Supplier<CountDownLatch>> downloadList;
    private ProgressMonitor monitor;


    @Override
    public void download() {

        if (downloadList == null || downloadList.isEmpty()) {
            return;
        }

        List<CountDownLatch> latchList = downloadList.stream().map(Supplier::get).toList();

        while (true) {

            Optional<Long> restTaskNum = latchList.stream().map(CountDownLatch::getCount).reduce(Long::sum);

            if (restTaskNum.isPresent()) {

                if (monitor != null) {
                    // 更新剩余任务的数量
                    monitor.updateRestTaskNum(restTaskNum.get());
                }

                if (restTaskNum.get() == 0) {
                    return;
                }
            }

        }
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
