package com.socia1ca3t.timepillbackup.core.download;

import com.socia1ca3t.timepillbackup.core.ImgDownloader;
import com.socia1ca3t.timepillbackup.core.ImgPathConvertor;
import com.socia1ca3t.timepillbackup.core.progress.ProgressMonitor;
import com.socia1ca3t.timepillbackup.pojo.dto.Diary;
import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;
import com.socia1ca3t.timepillbackup.pojo.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

/**
 * 图片的CDN服务器开启了图片防盗链，故需要下载图片，供前端展示时使用
 */
@Slf4j
public class ImgDownloaderBuilder {

    private final List<Supplier<CountDownLatch>> downloadList = new CopyOnWriteArrayList<>();

    private ProgressMonitor monitor;

    private final ImgDownloader.Mode mode;

    private final ImgPathConvertor imgPathconvertor;


    public static ImgDownloaderBuilder createSyncMode(ImgPathConvertor imgPathconvertor) {

        return new ImgDownloaderBuilder(imgPathconvertor, ImgDownloader.Mode.SYNC);
    }

    public static ImgDownloaderBuilder createAsyncMode(ImgPathConvertor imgPathconvertor) {

        return new ImgDownloaderBuilder(imgPathconvertor, ImgDownloader.Mode.ASYNC);
    }

    private ImgDownloaderBuilder(ImgPathConvertor convertor, ImgDownloader.Mode mode) {

        this.imgPathconvertor = convertor;
        this.mode = mode;
    }

    public ImgDownloaderBuilder userIcon(UserInfo userInfo) {

        downloadList.add(() -> {

            CountDownLatch latch = new CountDownLatch(1);

            TaskConsumer.getInstance().addTask(new TaskWrapper(() -> {


                int userId = userInfo.getId();
                String imgURL = userInfo.getCoverUrl();

                String dowloadAbPath = imgPathconvertor.getHomePageImgDownloadAbPath(userId);
                String fileName = ImgRealDownloader.downloadIcon(dowloadAbPath, imgURL);

                String imgSrc = imgPathconvertor.getHomePageImgSrcPath(userId) + fileName;

                userInfo.setIconImgSrc(imgSrc);
                latch.countDown();
            }));

            return latch;
        });

        return this;
    }

    public ImgDownloaderBuilder notebooksCover(List<NoteBook> hasCoverNotebooks) {


        if (CollectionUtils.isEmpty(hasCoverNotebooks)) {
            return this;
        }

        downloadList.add(() -> {

            CountDownLatch latch = new CountDownLatch(hasCoverNotebooks.size());

            TaskConsumer taskConsumer = TaskConsumer.getInstance();
            hasCoverNotebooks.forEach(hasCoverNotebook ->

                    taskConsumer.addTask(new TaskWrapper(() -> {

                        int userId = hasCoverNotebook.getUserId();
                        String imgURL = hasCoverNotebook.getCoverImgURL();

                        String dowloadAbPath = imgPathconvertor.getHomePageImgDownloadAbPath(userId);
                        String fileName = ImgRealDownloader.downloadCover(dowloadAbPath, imgURL);

                        String imgSrc = imgPathconvertor.getHomePageImgSrcPath(hasCoverNotebook.getUserId()) + fileName;

                        hasCoverNotebook.setCoverImgSrc(imgSrc);
                        latch.countDown();
                    }))
            );

            return latch;
        });

        return this;
    }

    public ImgDownloaderBuilder diaryImage(List<Diary> imgDiaryList, int userId) {


        if (CollectionUtils.isEmpty(imgDiaryList)) {
            return this;
        }

        downloadList.add(() -> {

            CountDownLatch latch = new CountDownLatch(imgDiaryList.size());

            TaskConsumer taskConsumer = TaskConsumer.getInstance();
            imgDiaryList.forEach(imgDiary ->

                    taskConsumer.addTask(new TaskWrapper(() -> {

                        int notebookId = imgDiary.getNotebookId();
                        String imgURL = imgDiary.getContentImgURL();

                        String dowloadAbPath = imgPathconvertor.getDiaryImgDownloadAbPath(userId, notebookId);
                        String fileName = ImgRealDownloader.downloadCover(dowloadAbPath, imgURL);

                        String imgSrc = imgPathconvertor.getDiaryImgSrcPath(userId, notebookId) + fileName;

                        imgDiary.setImgSrc(imgSrc);
                        latch.countDown();
                    }))
            );

            return latch;
        });

        return this;
    }

    public ImgDownloaderBuilder monitor(ProgressMonitor monitor) {

        this.monitor = monitor;
        return this;
    }

    public ImgDownloader build() {

        return new InnerImgDownloaderClient(
                downloadList,
                mode,
                monitor
        );
    }

}
