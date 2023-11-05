package com.socia1ca3t.timepillbackup.core.backup;

import com.socia1ca3t.timepillbackup.core.Backuper;
import com.socia1ca3t.timepillbackup.core.ImgPathConvertor;
import com.socia1ca3t.timepillbackup.core.download.ImgSyncDownloader;
import com.socia1ca3t.timepillbackup.pojo.dto.*;
import com.socia1ca3t.timepillbackup.pojo.vo.HomePageVO;
import com.socia1ca3t.timepillbackup.pojo.vo.NotebookStatisticDataVO;
import com.socia1ca3t.timepillbackup.service.CurrentUserTimepillApiService;
import com.socia1ca3t.timepillbackup.service.DataAnalysisService;
import com.socia1ca3t.timepillbackup.util.BackupUtil;
import com.socia1ca3t.timepillbackup.util.SpringContextUtil;
import com.socia1ca3t.timepillbackup.util.TimepillUtil;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AllNotebookHTMLBackuper extends AbstractHTMLBackuper {

    private static final Logger logger = LoggerFactory.getLogger(AllNotebookHTMLBackuper.class);


    // 下载的用户主页模板
    public static final String USER_HOME_TEMPLATE_PATH = "download/user_home_page";


    public AllNotebookHTMLBackuper(UserInfo userInfo, RestTemplate userBasicAuthRestTemplate) {

        super(new BackupInfo(userInfo.getName(),
                Backuper.Type.ALL, userInfo.getId()),
                new CurrentUserTimepillApiService(userBasicAuthRestTemplate));

        UserInfo copiedUserInfo = new UserInfo();
        BeanUtils.copyProperties(userInfo, copiedUserInfo);
        this.userInfo = copiedUserInfo;
    }


    @Override
    protected File generateFiles() throws IOException {


        // 获取所有日记
        final List<NoteBook> allNotebookList = this.getAllNotebooks();

        logger.info("下载所需备份文件中包含的所有图片，并填充正确的文件路径...");
        List<NotebookAndItsDiariesDTO> notebookAndItsDiariesDTOList = this.downloadAllImagesAndConverPath(allNotebookList);

        logger.info("开始生成通用文件...");
        this.commonFileGenerate();

        logger.info("开始生成用户主页文件...");
        this.userHomeHTMLGenerate(userInfo, allNotebookList);

        logger.info("开始生成日记的HTML文件...");
        this.allNotebookHTMLGenerate(notebookAndItsDiariesDTOList);


        return new File(getGenerateFilesPath());
    }


    /**
     * 下载所需备份文件中包含的所有图片
     */
    private List<NotebookAndItsDiariesDTO> downloadAllImagesAndConverPath(List<NoteBook> allNotebookList) {


        List<NotebookAndItsDiariesDTO> notebookAndItsDiariesDTOList = new ArrayList<>();
        List<Diary> allDiaryList = new ArrayList<>();

        logger.info("[{}]开始准备下载所有图片，日记本总数{}", userInfo.getName(), allNotebookList.size());

        if (!allNotebookList.isEmpty()) {

            // 获取所有日记
            allNotebookList.stream().parallel()
                    .forEach(noteBook -> {

                        List<Diary> diaryList = getAllDiaries(noteBook.getId());

                        allDiaryList.addAll(diaryList);
                        notebookAndItsDiariesDTOList.add(new NotebookAndItsDiariesDTO(noteBook, diaryList));
                    });
        }


        logger.info("[{}]开始准备下载所有图片，所有日记总数{}", userInfo.getName(), allDiaryList.size());

        List<Diary> allImageDiaryList = new ArrayList<>();
        if (!allDiaryList.isEmpty()) {

            // 获取所有带有图片的日记
            allImageDiaryList = allDiaryList.stream()
                    .filter(diary -> diary.getContentImgURL() != null)
                    .collect(Collectors.toList());
        }

        logger.info("[{}]开始准备下载所有图片，带图片的日记总数{}", userInfo.getName(), allImageDiaryList.size());

        downloaderBuilder
                .userIcon(userInfo)
                .notebooksCover(allNotebookList)
                .diaryImage(allImageDiaryList, userInfo.getId())
                .build(new ImgSyncDownloader()).download();

        return notebookAndItsDiariesDTOList;
    }


    /**
     * 生成所有日记本
     */
    private void allNotebookHTMLGenerate(List<NotebookAndItsDiariesDTO> notebookAndItsDiariesDTOList) {

        AtomicInteger num = new AtomicInteger(0);
        notebookAndItsDiariesDTOList.forEach(noteBookAndItsDiariesDTO -> {

            NoteBook noteBook = noteBookAndItsDiariesDTO.getNoteBook();
            logger.info("准备[{}]的第{}个日记本[{}]", userInfo.getName(), num.incrementAndGet(), noteBook.getName());

            String html = BackupUtil.generateSingleNotebookHTML(noteBookAndItsDiariesDTO, getDiaryTemplatePath());
            String targetFileURLWithId = getGenerateFilesPath() + "notebooks/" + noteBook.getId() + ".html";
            String targetFileURLWithName = getGenerateFilesPath() + "notebooks/" + noteBook.getId() + "_" + noteBook.getName() + ".html";

            BackupUtil.copyFile(html.getBytes(StandardCharsets.UTF_8), new File(targetFileURLWithId));
            BackupUtil.copyFile(html.getBytes(StandardCharsets.UTF_8), new File(targetFileURLWithName));
        });
    }

    private void commonFileGenerate() {

        // 准备通用的文件，如：CSS,系统图片
        String notebookBackgroudImg = TimepillUtil.getConfig().notebookBackgroudImg();
        if (!StringUtils.isNullOrEmpty(notebookBackgroudImg)) {
            BackupUtil.copyFile("images/" + notebookBackgroudImg, getGenerateFilesPath());
        }

        BackupUtil.copyFile("images/moya_cartoon_70.png", getGenerateFilesPath());
        BackupUtil.copyFile("images/default-cover.png", getGenerateFilesPath());
        BackupUtil.copyFile("css/bulma.min.css", getGenerateFilesPath());
        BackupUtil.copyFile("css/bulma-timeline.min.css", getGenerateFilesPath());
        BackupUtil.copyFile("js/jquery-3.1.1.min.js", getGenerateFilesPath());

    }


    /**
     * 生成用户主页
     */
    private void userHomeHTMLGenerate(UserInfo userInfo, List<NoteBook> allNotebookList) {

            NotebookStatisticDataVO statisticData = SpringContextUtil.getBean(DataAnalysisService.class)
                    .analysisNotebook(userInfo, allNotebookList);

            HomePageVO homePageVO = new HomePageVO(userInfo, statisticData, allNotebookList);

            logger.info("333333333333333333333333333333");
            Map<String, Object> context = new HashMap<>();
            context.put("homePageVO", homePageVO);
            context.put("notebookMap", TimepillUtil.groupByYear(allNotebookList));
            context.put("backgroudImg", TimepillUtil.getConfig().notebookBackgroudImg());

            logger.info("444444444444444444444444444444444444");
            String homeHtml = TimepillUtil.render2html(context, USER_HOME_TEMPLATE_PATH);
            String targetFileURL = getGenerateFilesPath() + userInfo.getName() + "的日记本.html";

            logger.info("55555555555555555555555555555555555555");

            BackupUtil.copyFile(homeHtml.getBytes(StandardCharsets.UTF_8), new File(targetFileURL));

    }


    @Override
    public String getGenerateFilesPath() {

        return ImgPathConvertor.FILE_BASE_PATH + userInfo.getId() + "/";
    }

    @Override
    public String getBackupZipFileName() {

        return userInfo.getId() + ".zip";
    }

    @Override
    public String getDiaryTemplatePath() {

        return "download/all_diary_index";
    }

}
