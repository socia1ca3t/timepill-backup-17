package com.socia1ca3t.timepillbackup.core.path;

import com.socia1ca3t.timepillbackup.core.ImgPathProducer;

public abstract class AbstractImgPathProducer implements ImgPathProducer {


    @Override
    public String getDiaryImgDownloadAbPath(int userId, int notebookId) {

        return DiaryImgPathProducer.getDownloadAbsolutePath(FILE_BASE_PATH, userId, notebookId);
    }


    @Override
    public String getHomePageImgDownloadAbPath(int userId) {

        return HomePageImgPathProducer.getDownloadAbsolutePath(FILE_BASE_PATH, userId);
    }


    /**
     * 获取日记中图片的路径，不包含文件名
     */
    protected static class DiaryImgPathProducer {


        public static String getDownloadAbsolutePath(String fileBasePath, int userId, int notebookId) {

            return fileBasePath + getRelativePathForShow(userId, notebookId);
        }

        public static String getRelativePathForDownload() {

            return DIARY_IMG_FOLDER_NAME + "/";
        }

        public static String getRelativePathForShow(int userId, int notebookId) {

            return "/" + userId + "/"+ NOTEBOOKS_FOLDER_NAME +"/" + notebookId + "/"+ DIARY_IMG_FOLDER_NAME +"/";
        }

    }


    /**
     * 获取用户主页中图片（头像、日记封面）的路径，不包含文件名
     */
    protected static class HomePageImgPathProducer {


        public static String getDownloadAbsolutePath(String fileBasePath, int userId) {

            return fileBasePath + getRelativePathForShow(userId);
        }

        public static String getRelativePathForDownload() {

            return HOMEPAGE_IMG_FOLDER_NAME + "/";
        }

        public static String getRelativePathForShow(int userId) {

            return "/" + userId + "/"+ HOMEPAGE_IMG_FOLDER_NAME +"/";
        }
    }

}
