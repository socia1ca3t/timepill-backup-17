package com.socia1ca3t.timepillbackup.service;

import com.socia1ca3t.timepillbackup.pojo.dto.DiaryDTO;
import com.socia1ca3t.timepillbackup.pojo.dto.NotebookDTO;
import com.socia1ca3t.timepillbackup.pojo.dto.UserDTO;
import com.socia1ca3t.timepillbackup.pojo.vo.DiariesStatisticData;
import com.socia1ca3t.timepillbackup.pojo.vo.NotebookStatisticDataVO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class DataAnalysisService {


    /**
     * 分析所有日记本的汇总信息
     */
    public NotebookStatisticDataVO analysisNotebook(UserDTO userInfo, List<NotebookDTO> noteBooks) {

        LocalDate birthDate = LocalDate.parse(userInfo.getCreated(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Period period = Period.between(birthDate, LocalDate.now());
        int age = period.getYears();
        int notebookNum = noteBooks.size();
        int expiredNum = (int) noteBooks.stream().filter(NotebookDTO::isHasExpired).count();
        int privateNum = (int) noteBooks.stream().filter(book -> !book.isToPublic()).count();

        return new NotebookStatisticDataVO(age, notebookNum, expiredNum, privateNum);
    }


    /**
     * 分析单本日记的汇总信息
     */
    public DiariesStatisticData analysisDiary(List<DiaryDTO> diaries) {

        int imagesDiariesNum = (int) diaries.stream().filter(diary -> diary.getContentImgURL() != null).count();

        DiariesStatisticData data = new DiariesStatisticData();
        data.setTotalNum(diaries.size());
        data.setImagesDiariesNum(imagesDiariesNum);
        data.setTextDiariesNum(diaries.size() - imagesDiariesNum);

        return data;
    }


}
