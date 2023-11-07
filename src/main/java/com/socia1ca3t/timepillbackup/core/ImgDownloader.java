package com.socia1ca3t.timepillbackup.core;

public interface ImgDownloader {


     enum Mode {
        SYNC,
        ASYNC
    }

    void download();

}
