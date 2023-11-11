package com.socia1ca3t.timepillbackup.pojo.vo;

import com.socia1ca3t.timepillbackup.pojo.dto.DiaryDTO;
import com.socia1ca3t.timepillbackup.pojo.dto.NotebookDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class DiariesIndexVO {

    private List<DiaryDTO> diaries;

    private DiariesStatisticData statisticData;

    private NotebookDTO noteBook;

}
