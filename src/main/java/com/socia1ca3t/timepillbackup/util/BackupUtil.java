package com.socia1ca3t.timepillbackup.util;

import com.socia1ca3t.timepillbackup.pojo.dto.NotebookAndItsDiariesDTO;
import com.socia1ca3t.timepillbackup.pojo.vo.DiariesIndexVO;
import com.socia1ca3t.timepillbackup.pojo.vo.DiariesStatisticData;
import com.socia1ca3t.timepillbackup.service.DataAnalysisService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BackupUtil {


    public static void copyFile(String relativeURL, String targetAbsolutePath) {


        Resource resource = new ClassPathResource("/static/" + relativeURL);

        try {
            String targetFileURL = targetAbsolutePath + relativeURL;
            createDirectories(targetFileURL);

            // 打在包中的资源，需要 getInputStream 类获取，无法通过 getFile 获取
            StreamUtils.copy(resource.getInputStream(), new FileOutputStream(targetFileURL));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void createDirectories(String targetFileURL) {

        try {
            Path targetDirectory = Paths.get(targetFileURL).getParent();
            Files.createDirectories(targetDirectory);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 生成单个日记本的HTML
     */
    public static String generateSingleNotebookHTML(NotebookAndItsDiariesDTO noteBookAndItsDiariesDTO, String diaryTemplatePath) {


        DiariesStatisticData statisticData = SpringContextUtil.getBean(DataAnalysisService.class).analysisDiary(noteBookAndItsDiariesDTO.getDiarieList());
        DiariesIndexVO diariesIndexVO = new DiariesIndexVO(noteBookAndItsDiariesDTO.getNoteBook(), noteBookAndItsDiariesDTO.getDiarieList(), statisticData);


        Map<String, Object> context = new HashMap<>();
        context.put("diariesIndexVO", diariesIndexVO);
        context.put("backgroudImg", TimepillUtil.getConfig().getNotebookBackgroudImg());


        return TimepillUtil.render2html(context, diaryTemplatePath);
    }

}
