package com.socia1ca3t.timepillbackup.service;

import com.socia1ca3t.timepillbackup.pojo.dto.Diary;
import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;
import com.socia1ca3t.timepillbackup.pojo.dto.UserInfo;
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
    public NotebookStatisticDataVO analysisNotebook(UserInfo userInfo, List<NoteBook> noteBooks) {

        LocalDate birthDate = LocalDate.parse(userInfo.getCreated(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Period period = Period.between(birthDate, LocalDate.now());
        int age = period.getYears();
        int notebookNum = noteBooks.size();
        int expiredNum = (int) noteBooks.stream().filter(NoteBook::isHasExpired).count();
        int privateNum = (int) noteBooks.stream().filter(book -> !book.isToPublic()).count();

        return new NotebookStatisticDataVO(age, notebookNum, expiredNum, privateNum);
    }


    /**
     * 分析单本日记的汇总信息
     */
    public DiariesStatisticData analysisDiary(List<Diary> diaries) {

        int imagesDiariesNum = (int) diaries.stream().filter(diary -> diary.getContentImgURL() != null).count();

        DiariesStatisticData data = new DiariesStatisticData();
        data.setTotalNum(diaries.size());
        data.setImagesDiariesNum(imagesDiariesNum);
        data.setTextDiariesNum(diaries.size() - imagesDiariesNum);

        return data;
    }


}
