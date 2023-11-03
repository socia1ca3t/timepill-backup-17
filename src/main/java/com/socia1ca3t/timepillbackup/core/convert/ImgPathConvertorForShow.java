package com.socia1ca3t.timepillbackup.core.convert;


/**
 * 前端显示用，下载远程资源，并返回基于notebookTemp路径的相对路径
 */
public class ImgPathConvertorForShow extends AbstractImgPathConvertor {


    @Override
    public String getDiaryImgSrcPath(int userId, int notebookId) {

        return DiaryImgPathConvertor.getRelativePathForShow(userId, notebookId);
    }


    @Override
    public String getHomePageImgSrcPath(int userId) {

        return HomePageImgPathConvertor.getRelativePathForShow(userId);
    }

}
