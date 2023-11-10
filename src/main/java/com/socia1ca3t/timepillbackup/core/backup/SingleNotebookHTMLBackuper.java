package com.socia1ca3t.timepillbackup.core.backup;

import com.socia1ca3t.timepillbackup.core.Backuper;
import com.socia1ca3t.timepillbackup.core.ImgPathProducer;
import com.socia1ca3t.timepillbackup.pojo.dto.*;
import com.socia1ca3t.timepillbackup.service.CurrentUserTimepillApiService;
import com.socia1ca3t.timepillbackup.util.BackupUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;


public class SingleNotebookHTMLBackuper extends AbstractHTMLBackuper {

    private static final Logger logger = LoggerFactory.getLogger(SingleNotebookHTMLBackuper.class);

    private static final String DIARY_TEMPLATE_PATH = "download/single_diary_index";

    // 需要下载的日记本
    private final NoteBook noteBook;
    private List<Diary> allDiaryList;
    private List<ImgDownloadInfo> needDownloadImgs;


    public SingleNotebookHTMLBackuper(NoteBook noteBook, UserInfo userInfo, RestTemplate userBasicAuthRestTemplate) {


        super(userInfo, new BackupInfo(userInfo.getName(),
                Backuper.Type.SINGLE,
                noteBook.getId()),
                new CurrentUserTimepillApiService(userBasicAuthRestTemplate));

        NoteBook copiedNoteBook = new NoteBook();
        BeanUtils.copyProperties(noteBook, copiedNoteBook);
        this.noteBook = copiedNoteBook;
    }

    @Override
    protected void initData() {


        this.allDiaryList = getAllDiaries(noteBook.getId());

        // 获取所有带有图片的日记
        List<Diary> allImageDiaryList = allDiaryList.stream()
                .filter(diary -> diary.getContentImgURL() != null)
                .collect(Collectors.toList());

        needDownloadImgs = imgPathSetter.diaryImage(allImageDiaryList);
    }

    @Override
    public File generateFiles() {

        logger.info("{}开始准备单个日记本[{}]", userInfo.getEmail(), noteBook.getName());

        // 生成通用的文件，如：CSS,系统图片
        BackupUtil.copyFile("css/bulma.min.css", getGenerateFilesPath());

        // 生成日记的HTML文件
        String html = BackupUtil.generateSingleNotebookHTML(new NotebookAndItsDiariesDTO(noteBook, allDiaryList), getDiaryTemplatePath());

        String targetFileURLWithId = getGenerateFilesPath() + noteBook.getId() + ".html";
        String targetFileURLWithName = getGenerateFilesPath() + noteBook.getId() + "_"
                                        + BackupUtil.filterIllegalCharOfFilePath(noteBook.getName()) + ".html";

        BackupUtil.generateNotebookFile(html, targetFileURLWithId, targetFileURLWithName);

        return new File(getGenerateFilesPath());
    }


    @Override
    public List<ImgDownloadInfo> getAllImageDownloadInfo() {

        return needDownloadImgs;
    }


    @Override
    public String getGenerateFilesPath() {

        return ImgPathProducer.FILE_BASE_PATH + userInfo.getId() + "/notebooks/" + noteBook.getId() + "/";
    }

    @Override
    public String getDiaryTemplatePath() {

        return DIARY_TEMPLATE_PATH;
    }

    public String getBackupZipFileName() {

        return noteBook.getId() + ".zip";
    }

}
