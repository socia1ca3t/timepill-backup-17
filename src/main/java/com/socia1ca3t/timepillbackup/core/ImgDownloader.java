package com.socia1ca3t.timepillbackup.core;

import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

public interface ImgDownloader {

    void download();

    void setDownloadList(List<Supplier<CountDownLatch>> downloadList);

    void setMonitor(ProgressMonitor monitor);
}
