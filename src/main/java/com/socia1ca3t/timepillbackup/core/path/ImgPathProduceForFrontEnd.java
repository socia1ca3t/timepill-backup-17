package com.socia1ca3t.timepillbackup.core.path;


/**
 * 前端显示用，获得图片在 HTML 中 img 标签的 src 路径，以及图片下载之后的存储路径
 */
public class ImgPathProduceForFrontEnd extends AbstractImgPathProducer {


    @Override
    public String getDiaryImgSrcPath(int userId, int notebookId) {

        return DiaryImgPathProducer.getRelativePathForShow(userId, notebookId);
    }


    @Override
    public String getHomePageImgSrcPath(int userId) {

        return HomePageImgPathProducer.getRelativePathForShow(userId);
    }

}
