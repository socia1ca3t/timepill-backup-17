package com.socia1ca3t.timepillbackup.util;

import com.socia1ca3t.timepillbackup.pojo.dto.NotebookAndItsDiariesDTO;
import com.socia1ca3t.timepillbackup.pojo.vo.DiariesIndexVO;
import com.socia1ca3t.timepillbackup.pojo.vo.DiariesStatisticData;
import com.socia1ca3t.timepillbackup.service.DataAnalysisService;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BackupUtil {


    @SneakyThrows
    public static void copyFile(String relativeURL, String targetAbsolutePath) {


        Resource resource = new ClassPathResource("/static/" + relativeURL);

        File targetFile = new File(targetAbsolutePath + relativeURL);
        // 打在包中的资源，需要 getInputStream 类获取，无法通过 getFile 获取
        FileCopyUtils.copy(resource.getInputStream(), new FileOutputStream(makeDirs(targetFile)));

    }

    @SneakyThrows
    public static void copyFile(@NotNull byte[] in, @NotNull File out) {

        FileCopyUtils.copy(in, makeDirs(out));
    }

    @SneakyThrows
    public static File makeDirs(File file) {

        Path targetDirectory = Paths.get(file.getAbsolutePath()).getParent();
        Files.createDirectories(targetDirectory);

        return file;
    }

    /**
     * 生成单个日记本的HTML
     */
    public static String generateSingleNotebookHTML(NotebookAndItsDiariesDTO noteBookAndItsDiariesDTO, String diaryTemplatePath) {


        DiariesStatisticData statisticData = SpringContextUtil.getBean(DataAnalysisService.class).analysisDiary(noteBookAndItsDiariesDTO.getDiarieList());
        DiariesIndexVO diariesIndexVO = new DiariesIndexVO(noteBookAndItsDiariesDTO.getDiarieList(),
                                                            statisticData,
                                                            noteBookAndItsDiariesDTO.getNoteBook());

        Map<String, Object> context = new HashMap<>();
        context.put("diariesIndexVO", diariesIndexVO);

        String html = TimepillUtil.render2html(context, diaryTemplatePath);
        if (!StringUtils.hasText(html)) {
            throw new RuntimeException(diaryTemplatePath + "渲染 HTML 失败");
        }

        return html;
    }

    public static void generateNotebookFile(String html, String targetFileURLWithId, String targetFileURLWithName) {

        File notebookHtmlWithId = new File(targetFileURLWithId);
        File notebookHtmlWithName = new File(targetFileURLWithName);
        BackupUtil.copyFile(html.getBytes(StandardCharsets.UTF_8), notebookHtmlWithId);
        BackupUtil.copyFile(html.getBytes(StandardCharsets.UTF_8), notebookHtmlWithName);

        if (!notebookHtmlWithId.exists() || notebookHtmlWithId.length() == 0) {
            throw new RuntimeException(notebookHtmlWithId + "生成文件失败");
        }
        if (!notebookHtmlWithName.exists() || notebookHtmlWithName.length() == 0) {
            throw new RuntimeException(notebookHtmlWithName + "生成文件失败");
        }
    }

    public static String filterIllegalCharOfFilePath(String str) {

        return str.replaceAll("/", "")
                .replaceAll(":", "")
                .replaceAll("\\*", "")
                .replaceAll("\\?", "")
                .replaceAll("\"", "")
                .replaceAll("<", "")
                .replaceAll(">", "")
                .replaceAll("\\|", "");
    }

}
