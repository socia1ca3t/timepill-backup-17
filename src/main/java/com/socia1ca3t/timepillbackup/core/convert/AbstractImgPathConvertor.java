package com.socia1ca3t.timepillbackup.core.convert;

import com.socia1ca3t.timepillbackup.core.ImgPathConvertor;

public abstract class AbstractImgPathConvertor implements ImgPathConvertor {


    @Override
    public String getDiaryImgDownloadAbPath(int userId, int notebookId) {

        return DiaryImgPathConvertor.getDownloadAbsolutePath(FILE_BASE_PATH, userId, notebookId);
    }


    @Override
    public String getHomePageImgDownloadAbPath(int userId) {

        return HomePageImgPathConvertor.getDownloadAbsolutePath(FILE_BASE_PATH, userId);
    }


    /**
     * 获取日记中图片的路径，不包含文件名
     */
    protected static class DiaryImgPathConvertor {


        public static String getDownloadAbsolutePath(String fileBasePath, int userId, int notebookId) {

            return fileBasePath + getRelativePathForShow(userId, notebookId);
        }

        public static String getRelativePathForDownload() {

            return "images/";
        }

        public static String getRelativePathForShow(int userId, int notebookId) {

            return "/" + userId + "/notebooks/" + notebookId + "/images/";
        }

    }


    /**
     * 获取用户主页中图片（头像、日记封面）的路径，不包含文件名
     */
    protected static class HomePageImgPathConvertor {


        public static String getDownloadAbsolutePath(String fileBasePath, int userId) {

            return fileBasePath + getRelativePathForShow(userId);
        }

        public static String getRelativePathForDownload() {

            return "images/";
        }

        public static String getRelativePathForShow(int userId) {

            return "/" + userId + "/images/";
        }
    }

}
