package com.socia1ca3t.timepillbackup.core.path;

import com.socia1ca3t.timepillbackup.core.ImgPathProducer;
import com.socia1ca3t.timepillbackup.core.ImgPathSetter;
import com.socia1ca3t.timepillbackup.core.download.ImgRealDownloader;
import com.socia1ca3t.timepillbackup.pojo.dto.Diary;
import com.socia1ca3t.timepillbackup.pojo.dto.ImgDownloadInfo;
import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;
import com.socia1ca3t.timepillbackup.pojo.dto.UserInfo;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class MyImgPathSetter implements ImgPathSetter {


    private final ImgPathProducer imgPathProducer;

    public MyImgPathSetter(ImgPathProducer imgPathProducer) {
        this.imgPathProducer = imgPathProducer;
    }


    @Override
    public ImgDownloadInfo userIcon(UserInfo userInfo) {

        String imgURL = userInfo.getCoverUrl();
        String fileName = ImgRealDownloader.getOriginalFileName(imgURL);
        String srcPath = imgPathProducer.getHomePageImgSrcPath(userInfo.getId());

        userInfo.setIconImgSrc(srcPath + fileName);

        return new ImgDownloadInfo(imgURL,
                                    imgPathProducer.getHomePageImgDownloadAbPath(userInfo.getId()),
                                    fileName);
    }

    @Override
    public List<ImgDownloadInfo> notebooksCover(List<NoteBook> hasCoverNotebooks) {


        final List<ImgDownloadInfo> downloadList = new ArrayList<>();
        if (CollectionUtils.isEmpty(hasCoverNotebooks)) {
            return downloadList;
        }

        hasCoverNotebooks.forEach(hasCoverNotebook -> {

            int userId = hasCoverNotebook.getUserId();
            String imgURL = hasCoverNotebook.getCoverImgURL();
            String fileName = ImgRealDownloader.getOriginalFileName(imgURL);
            String srcPath = imgPathProducer.getHomePageImgSrcPath(userId);

            hasCoverNotebook.setCoverImgSrc(srcPath + fileName);

            downloadList.add(new ImgDownloadInfo(imgURL,
                                                imgPathProducer.getHomePageImgDownloadAbPath(userId),
                                                fileName));
        });

        return downloadList;
    }

    @Override
    public List<ImgDownloadInfo> diaryImage(List<Diary> imgDiaryList) {

        final List<ImgDownloadInfo> downloadList = new ArrayList<>();
        if (CollectionUtils.isEmpty(imgDiaryList)) {
            return downloadList;
        }

        imgDiaryList.forEach(imgDiary -> {

                int userId = imgDiary.getUserId();
                int notebookId = imgDiary.getNotebookId();
                String imgURL = imgDiary.getContentImgURL();
                String fileName = ImgRealDownloader.getOriginalFileName(imgURL);
                String imgSrc = imgPathProducer.getDiaryImgSrcPath(userId, notebookId) + fileName;

                imgDiary.setImgSrc(imgSrc);

                downloadList.add(new ImgDownloadInfo(imgURL,
                                                    imgPathProducer.getDiaryImgDownloadAbPath(userId, notebookId),
                                                    fileName));
            }
        );

        return downloadList;

    }
}
