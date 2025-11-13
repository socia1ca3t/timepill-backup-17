package com.socia1ca3t.timepillbackup.controller;

import com.socia1ca3t.timepillbackup.config.CurrentUser;
import com.socia1ca3t.timepillbackup.config.CurrentUserBasicAuthRestTemplate;
import com.socia1ca3t.timepillbackup.core.download.ImgDownloaderClient;
import com.socia1ca3t.timepillbackup.core.path.ImgPathProduceForFrontEnd;
import com.socia1ca3t.timepillbackup.core.path.ImgTagSrcPathSetterForHtml;
import com.socia1ca3t.timepillbackup.pojo.dto.DiaryDTO;
import com.socia1ca3t.timepillbackup.pojo.dto.NotebookDTO;
import com.socia1ca3t.timepillbackup.pojo.dto.UserDTO;
import com.socia1ca3t.timepillbackup.pojo.vo.DiariesIndexVO;
import com.socia1ca3t.timepillbackup.pojo.vo.DiariesStatisticData;
import com.socia1ca3t.timepillbackup.pojo.vo.HomePageVO;
import com.socia1ca3t.timepillbackup.pojo.vo.NotebookStatisticDataVO;
import com.socia1ca3t.timepillbackup.service.CurrentUserTimepillApiService;
import com.socia1ca3t.timepillbackup.service.DataAnalysisService;
import com.socia1ca3t.timepillbackup.util.TimepillUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class ViewController {

    private static final Log logger = LogFactory.getLog(ViewController.class);
    private final DataAnalysisService dataAnalysisService;
    @Autowired
    public ViewController(DataAnalysisService dataAnalysisService) {
        this.dataAnalysisService = dataAnalysisService;
    }
    protected final ImgTagSrcPathSetterForHtml imgPathSetter = new ImgTagSrcPathSetterForHtml(new ImgPathProduceForFrontEnd());

    @RequestMapping("/home")
    public String userHome(@CurrentUser UserDTO userInfo,
                           @CurrentUserBasicAuthRestTemplate RestTemplate restTemplate,
                           Model model) {



        // 下载用户头像并转换路径
        ImgDownloaderClient.createSyncMode(Collections.singletonList(imgPathSetter.userIcon(userInfo))).download();

        List<NotebookDTO> noteBooks = new CurrentUserTimepillApiService(restTemplate).getCachableNotebookList();
        logger.info(userInfo.getName() + "日记本数量" + noteBooks.size());

        List<NotebookDTO> hasCoverNotebooklist = noteBooks.stream()
                .filter(notebook -> notebook.getCoverImgURL() != null)
                .collect(Collectors.toList());

        if (!hasCoverNotebooklist.isEmpty()) {
            // 下载日记本封面并转换路径
            ImgDownloaderClient.createSyncMode(imgPathSetter.notebooksCover(hasCoverNotebooklist)).download();
        }

        // 数据分析
        NotebookStatisticDataVO statisticData = dataAnalysisService.analysisNotebook(userInfo, noteBooks);

        // 封装所有数据
        HomePageVO homePageVO = new HomePageVO(userInfo, statisticData, noteBooks);
        model.addAttribute("homePageVO", homePageVO);
        model.addAttribute("notebookMap", TimepillUtil.groupByYear(noteBooks));
        model.addAttribute("backgroudImg", TimepillUtil.getConfig().notebookBackgroudImg());

        logger.info(userInfo.getEmail() + "_" + userInfo.getName() + "进入了个人主页...");

        return "user_home_page";
    }

    @GetMapping("/notebook/{notebookId}")
    public String notebook(@CurrentUser UserDTO userInfo,
                           @PathVariable int notebookId,
                           @CurrentUserBasicAuthRestTemplate RestTemplate restTemplate,
                           Model model) {

        CurrentUserTimepillApiService currentUserApiService = new CurrentUserTimepillApiService(restTemplate);


        // 下载所有日记的图片并转换
        List<DiaryDTO> diaries = currentUserApiService.getCachableDiaryList(notebookId);
        List<DiaryDTO> imgDiaryList = diaries.stream().filter(diary -> diary.getContentImgURL() != null).collect(Collectors.toList());
        ImgDownloaderClient.createSyncMode(imgPathSetter.diaryImage(imgDiaryList)).download();

        // 获取该日记
        List<NotebookDTO> list = currentUserApiService.getCachableNotebookList();
        NotebookDTO noteBook = TimepillUtil.getNotebookById(list, notebookId);
        // 分析日记本数据
        DiariesStatisticData statisticData = dataAnalysisService.analysisDiary(diaries);
        DiariesIndexVO diariesIndexVO = new DiariesIndexVO(diaries, statisticData, noteBook);

        model.addAttribute("backgroudImg", TimepillUtil.getConfig().notebookBackgroudImg());
        model.addAttribute("diariesIndexVO", diariesIndexVO);

        return "diary_index";
    }

    @GetMapping("/downloadSomeNotebooks")
    public String downloadSomeNotebooks(@CurrentUser UserDTO userInfo,
                                        @CurrentUserBasicAuthRestTemplate RestTemplate restTemplate,
                                        Model model) {

        List<NotebookDTO> noteBooks = new CurrentUserTimepillApiService(restTemplate).getCachableNotebookList();
        logger.info(userInfo.getName() + "日记本数量" + noteBooks.size());

        List<NotebookDTO> hasCoverNotebooklist = noteBooks.stream()
                .filter(notebook -> notebook.getCoverImgURL() != null)
                .collect(Collectors.toList());

        if (!hasCoverNotebooklist.isEmpty()) {
            // 下载日记本封面并转换路径
            ImgDownloaderClient.createSyncMode(imgPathSetter.notebooksCover(hasCoverNotebooklist)).download();
        }

        // 数据分析
        NotebookStatisticDataVO statisticData = dataAnalysisService.analysisNotebook(userInfo, noteBooks);

        // 封装所有数据
        HomePageVO homePageVO = new HomePageVO(userInfo, statisticData, noteBooks);
        model.addAttribute("homePageVO", homePageVO);
        model.addAttribute("notebookMap", TimepillUtil.groupByYear(noteBooks));
        model.addAttribute("backgroudImg", TimepillUtil.getConfig().notebookBackgroudImg());

        return "download_some_notebooks";
    }

}
