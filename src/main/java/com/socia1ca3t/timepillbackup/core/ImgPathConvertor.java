package com.socia1ca3t.timepillbackup.core;

import com.socia1ca3t.timepillbackup.properties.TimepillConfig;
import com.socia1ca3t.timepillbackup.util.SpringContextUtil;

/**
 * 获得图片下载时的据对路径，以及在 HTML 中作为图片表示时的 src 相对路径
 */
public interface ImgPathConvertor {

    String FILE_BASE_PATH = SpringContextUtil.getBean(TimepillConfig.class).getFileBasePath();

    String getDiaryImgDownloadAbPath(int userId, int notebookId);


    String getDiaryImgSrcPath(int userId, int notebookId);


    String getHomePageImgDownloadAbPath(int userId);


    String getHomePageImgSrcPath(int userId);

}
