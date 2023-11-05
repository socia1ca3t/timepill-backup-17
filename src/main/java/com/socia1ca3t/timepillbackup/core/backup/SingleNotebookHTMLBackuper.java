package com.socia1ca3t.timepillbackup.core.backup;

import com.socia1ca3t.timepillbackup.core.Backuper;
import com.socia1ca3t.timepillbackup.core.ImgPathConvertor;
import com.socia1ca3t.timepillbackup.core.download.ImgSyncDownloader;
import com.socia1ca3t.timepillbackup.pojo.dto.*;
import com.socia1ca3t.timepillbackup.service.CurrentUserTimepillApiService;
import com.socia1ca3t.timepillbackup.util.BackupUtil;
import com.socia1ca3t.timepillbackup.util.TimepillUtil;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;


public class SingleNotebookHTMLBackuper extends AbstractHTMLBackuper {

    private static final Logger logger = LoggerFactory.getLogger(SingleNotebookHTMLBackuper.class);

    private static final String DIARY_TEMPLATE_PATH = "download/single_diary_index";


    // 需要下载的日记本
    private final NoteBook noteBook;


    public SingleNotebookHTMLBackuper(NoteBook noteBook, UserInfo userInfo, RestTemplate userBasicAuthRestTemplate) {


        super(new BackupInfo(userInfo.getName(),
                Backuper.Type.SINGLE,
                noteBook.getId()),
                new CurrentUserTimepillApiService(userBasicAuthRestTemplate));

        NoteBook copiedNoteBook = new NoteBook();
        BeanUtils.copyProperties(noteBook, copiedNoteBook);
        this.noteBook = copiedNoteBook;

        UserInfo copiedUserInfo = new UserInfo();
        BeanUtils.copyProperties(userInfo, copiedUserInfo);
        this.userInfo = copiedUserInfo;
    }


    @Override
    public File generateFiles() throws IOException {

        logger.info("{}开始准备单个日记本[{}]", userInfo.getEmail(), noteBook.getName());

        final List<Diary> allDiaryList = getAllDiaries(noteBook.getId());


        // 获取所有带有图片的日记
        List<Diary> allImageDiaryList = allDiaryList.stream()
                .filter(diary -> diary.getContentImgURL() != null)
                .collect(Collectors.toList());


        downloaderBuilder.diaryImage(allImageDiaryList, userInfo.getId()).build(new ImgSyncDownloader()).download();

        // 生成通用的文件，如：CSS,系统图片
        String notebookBackgroudImg = TimepillUtil.getConfig().notebookBackgroudImg();
        if (!StringUtils.isNullOrEmpty(notebookBackgroudImg)) {
            BackupUtil.copyFile("images/" + notebookBackgroudImg, getGenerateFilesPath());
        }
        BackupUtil.copyFile("css/bulma.min.css", getGenerateFilesPath());

        // 生成日记的HTML文件
        String html = BackupUtil.generateSingleNotebookHTML(new NotebookAndItsDiariesDTO(noteBook, allDiaryList), getDiaryTemplatePath());

        String targetFileURL = getGenerateFilesPath() + noteBook.getId() + "_" + noteBook.getName() + ".html";

        BackupUtil.copyFile(html.getBytes(StandardCharsets.UTF_8), new File(targetFileURL));

        return new File(getGenerateFilesPath());
    }


    @Override
    public String getGenerateFilesPath() {

        return ImgPathConvertor.FILE_BASE_PATH + userInfo.getId() + "/notebooks/" + noteBook.getId() + "/";
    }

    @Override
    public String getDiaryTemplatePath() {

        return DIARY_TEMPLATE_PATH;
    }

    public String getBackupZipFileName() {

        return noteBook.getId() + ".zip";
    }

}
