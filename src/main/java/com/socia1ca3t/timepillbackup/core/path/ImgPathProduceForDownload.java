package com.socia1ca3t.timepillbackup.core.path;


/**
 * 准备压缩文件时使用，下载远程资源，并返回基于用户临时文件夹的相对路径
 */
public class ImgPathProduceForDownload extends AbstractImgPathProducer {


    @Override
    public String getDiaryImgSrcPath(int userId, int notebookId) {

        return DiaryImgPathConvertor.getRelativePathForDownload();
    }


    @Override
    public String getHomePageImgSrcPath(int userId) {

        return HomePageImgPathConvertor.getRelativePathForDownload();
    }

}
