package com.socia1ca3t.timepillbackup.core;

public interface ImgDownloader {


     enum Mode {
        SYNC,
        ASYNC
    }

    enum ImgType {
        USER_ICON,
        NOTEBOOK_COVER,
        DIARY_IMG
    }

    void download();

}
