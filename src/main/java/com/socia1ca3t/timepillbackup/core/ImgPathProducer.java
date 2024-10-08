package com.socia1ca3t.timepillbackup.core;

import java.io.File;

/**
 * 获得图片下载时的绝对路径，以及在 HTML 中作为图片表示时的 src 相对路径
 */
public interface ImgPathProducer {

    String FILE_BASE_PATH = System.getProperty("java.io.tmpdir") + File.separator + "timepill-backup" + File.separator;

    String NOTEBOOKS_FOLDER_NAME = "notebooks";
    String DIARY_IMG_FOLDER_NAME = "diary-images";
    String HOMEPAGE_IMG_FOLDER_NAME = "images";

    String getDiaryImgDownloadAbPath(int userId, int notebookId);


    String getDiaryImgSrcPath(int userId, int notebookId);


    String getHomePageImgDownloadAbPath(int userId);


    String getHomePageImgSrcPath(int userId);

}
