package com.socia1ca3t.timepillbackup.core.path;


/**
 * 备份文件时使用，获得图片在 HTML 中 img 标签的 src 路径，以及图片下载之后的存储路径
 */
public class ImgPathProduceForBackup extends AbstractImgPathProducer {


    @Override
    public String getDiaryImgSrcPath(int userId, int notebookId) {

        return DiaryImgPathProducer.getRelativePathForDownload();
    }


    @Override
    public String getHomePageImgSrcPath(int userId) {

        return HomePageImgPathProducer.getRelativePathForDownload();
    }

}
