package com.socia1ca3t.timepillbackup.core.backup;

import com.socia1ca3t.timepillbackup.core.Backuper;
import com.socia1ca3t.timepillbackup.core.ImgPathProducer;
import com.socia1ca3t.timepillbackup.pojo.dto.*;
import com.socia1ca3t.timepillbackup.pojo.vo.HomePageVO;
import com.socia1ca3t.timepillbackup.pojo.vo.NotebookStatisticDataVO;
import com.socia1ca3t.timepillbackup.service.CurrentUserTimepillApiService;
import com.socia1ca3t.timepillbackup.service.DataAnalysisService;
import com.socia1ca3t.timepillbackup.util.BackupUtil;
import com.socia1ca3t.timepillbackup.util.SpringContextUtil;
import com.socia1ca3t.timepillbackup.util.TimepillUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class AllNotebookHTMLBackuper extends AbstractHTMLBackuper {

    public static final String USER_HOME_TEMPLATE_PATH = "download/user_home_page";
    public static final String DIARY_TEMPLATE_PATH = "download/all_diary_index";

    private final List<NotebookDTO> allNotebookList = super.getAllNotebooks();

    private List<ImgDownloadInfo> needDownloadImgs;
    private List<DiaryDTO> allDiaryList;
    private List<NotebookAndItsDiariesDTO> notebookAndItsDiariesDTOList;

    public AllNotebookHTMLBackuper(UserDTO userInfo, RestTemplate userBasicAuthRestTemplate) {

        super(userInfo, new BackupInfo(userInfo.getName(),
                Backuper.Type.ALL, userInfo.getId()),
                new CurrentUserTimepillApiService(userBasicAuthRestTemplate));
    }

    @Override
    protected void initData() {

        allDiaryList = new ArrayList<>();
        notebookAndItsDiariesDTOList = new ArrayList<>();

        if (!allNotebookList.isEmpty()) {

            // 获取所有日记
            allNotebookList.stream().parallel()
                    .forEach(noteBook -> {

                        List<DiaryDTO> singleNotbookDiary = super.getAllDiaries(noteBook.getId());
                        allDiaryList.addAll(singleNotbookDiary);
                        notebookAndItsDiariesDTOList.add(new NotebookAndItsDiariesDTO(noteBook, singleNotbookDiary));
                    });
        }
        needDownloadImgs = doHTMLSrcPathSet();
    }

    private List<ImgDownloadInfo> doHTMLSrcPathSet() {

        List<DiaryDTO> allImageDiaryList = null;

        if (!allDiaryList.isEmpty()) {
            // 获取所有图片日记
            allImageDiaryList = allDiaryList.stream().parallel().filter(diary -> diary.getContentImgURL() != null).toList();
        }

        List<ImgDownloadInfo> list = new ArrayList<>();
        list.add(imgPathSetter.userIcon(userInfo));
        list.addAll(imgPathSetter.notebooksCover(allNotebookList));
        list.addAll(imgPathSetter.diaryImage(allImageDiaryList));

        return list;
    }

    @Override
    protected File generateFiles() {

        log.info("开始生成通用文件...");
        commonFileGenerate(getGenerateFilesPath());

        log.info("开始生成用户主页文件...");
        userHomeHTMLGenerate(userInfo, allNotebookList, getGenerateFilesPath());

        log.info("开始生成日记的HTML文件...");
        allNotebookHTMLGenerate(notebookAndItsDiariesDTOList, getGenerateFilesPath(), userInfo.getName());


        return new File(getGenerateFilesPath());
    }


    @Override
    public List<ImgDownloadInfo> getAllImageInfoWaitForDownload() {

        return needDownloadImgs;
    }

    /**
     * 生成所有日记本
     */
    private static void allNotebookHTMLGenerate(final List<NotebookAndItsDiariesDTO> notebookAndItsDiariesDTOList,
                                                final String baseFilePath, final String userName) {

        AtomicInteger num = new AtomicInteger(0);
        notebookAndItsDiariesDTOList.forEach(noteBookAndItsDiariesDTO -> {

            NotebookDTO noteBook = noteBookAndItsDiariesDTO.getNoteBook();
            log.info("准备[{}]的第{}个日记本[{}]", userName, num.incrementAndGet(), noteBook.getName());

            String html = BackupUtil.generateSingleNotebookHTML(noteBookAndItsDiariesDTO, DIARY_TEMPLATE_PATH);

            String targetFileURLWithId = baseFilePath + "notebooks/" + noteBook.getId() + ".html";
            String targetFileURLWithName = baseFilePath + "notebooks/" + noteBook.getId() + "_"
                    + BackupUtil.filterIllegalCharOfFilePath(noteBook.getName()) + ".html";

            BackupUtil.generateNotebookFile(html, targetFileURLWithId, targetFileURLWithName);
        });


        File files = new File(baseFilePath + "notebooks/");
        File[] allHTMLFiles = files.listFiles((file, fileName) -> fileName.endsWith("html"));

        if (allHTMLFiles == null) {
            log.info("共生成日记本的 HTML 0 个");
            return;
        }
        log.info("共生成日记本的 HTML {} 个", allHTMLFiles.length);
        if (allHTMLFiles.length != notebookAndItsDiariesDTOList.size() * 2) {

            throw new RuntimeException(String.format("日记本的 HTML 文件生成异常，重新生成，正确数量%d，实际数量%d",
                                                    notebookAndItsDiariesDTOList.size() * 2,
                                                    allHTMLFiles.length));
        }

    }

    private static void commonFileGenerate(String baseFilePath) {

        BackupUtil.copyFile("images/moya_cartoon_70.png", baseFilePath);
        BackupUtil.copyFile("images/default-cover.png", baseFilePath);
        BackupUtil.copyFile("css/bulma.min.css", baseFilePath);
        BackupUtil.copyFile("css/bulma-timeline.min.css", baseFilePath);
        BackupUtil.copyFile("js/jquery-3.1.1.min.js", baseFilePath);
        BackupUtil.copyFile("icons/js/all.min.js", baseFilePath);
    }


    /**
     * 生成用户主页
     */
    private static void userHomeHTMLGenerate(UserDTO userInfo, List<NotebookDTO> allNotebookList, String baseFilePath) {

            NotebookStatisticDataVO statisticData = SpringContextUtil.getBean(DataAnalysisService.class)
                    .analysisNotebook(userInfo, allNotebookList);

            HomePageVO homePageVO = new HomePageVO(userInfo, statisticData, allNotebookList);

            Map<String, Object> context = new HashMap<>();
            context.put("homePageVO", homePageVO);
            context.put("notebookMap", TimepillUtil.groupByYear(allNotebookList));
            context.put("backgroudImg", TimepillUtil.getConfig().notebookBackgroudImg());

            String homeHtml = TimepillUtil.render2html(context, USER_HOME_TEMPLATE_PATH);
            String targetFileURL = baseFilePath + userInfo.getName() + "的日记本.html";

            BackupUtil.copyFile(homeHtml.getBytes(StandardCharsets.UTF_8), new File(targetFileURL));

    }


    @Override
    public String getGenerateFilesPath() {

        return ImgPathProducer.FILE_BASE_PATH + userInfo.getId() + File.separator;
    }

    @Override
    public String getBackupZipFileName() {

        return userInfo.getId() + ".zip";
    }

    @Override
    public String getDiaryTemplatePath() {

        return DIARY_TEMPLATE_PATH;
    }

}
