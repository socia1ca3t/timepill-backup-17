package com.socia1ca3t.timepillbackup.core.path;

import com.socia1ca3t.timepillbackup.core.ImgDownloader;
import com.socia1ca3t.timepillbackup.core.ImgPathProducer;
import com.socia1ca3t.timepillbackup.core.ImgPathSetter;
import com.socia1ca3t.timepillbackup.core.download.ImgRealDownloader;
import com.socia1ca3t.timepillbackup.pojo.dto.DiaryDTO;
import com.socia1ca3t.timepillbackup.pojo.dto.ImgDownloadInfo;
import com.socia1ca3t.timepillbackup.pojo.dto.NotebookDTO;
import com.socia1ca3t.timepillbackup.pojo.dto.UserDTO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置图片在 HTML 中 img 标签的 src 路径
 */
public class ImgTagSrcPathSetterForHtml implements ImgPathSetter {


    private final ImgPathProducer imgPathProducer;

    public ImgTagSrcPathSetterForHtml(ImgPathProducer imgPathProducer) {
        this.imgPathProducer = imgPathProducer;
    }


    @Override
    public ImgDownloadInfo userIcon(UserDTO userInfo) {

        String imgURL = userInfo.getCoverUrl();
        String fileName = ImgRealDownloader.getOriginalFileName(imgURL);
        String srcPath = imgPathProducer.getHomePageImgSrcPath(userInfo.getId());

        userInfo.setIconImgSrc(srcPath + fileName);

        return new ImgDownloadInfo(imgURL,
                                    imgPathProducer.getHomePageImgDownloadAbPath(userInfo.getId()),
                                    fileName, ImgDownloader.ImgType.USER_ICON);
    }

    @Override
    public List<ImgDownloadInfo> notebooksCover(List<NotebookDTO> hasCoverNotebooks) {


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
                                                fileName, ImgDownloader.ImgType.NOTEBOOK_COVER));
        });

        return downloadList;
    }

    @Override
    public List<ImgDownloadInfo> diaryImage(List<DiaryDTO> imgDiaryList) {

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
                                                    fileName, ImgDownloader.ImgType.DIARY_IMG));
            }
        );

        return downloadList;

    }
}
