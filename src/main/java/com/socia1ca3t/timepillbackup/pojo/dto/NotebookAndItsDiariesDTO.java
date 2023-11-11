package com.socia1ca3t.timepillbackup.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NotebookAndItsDiariesDTO {

    private NotebookDTO noteBook;

    private List<DiaryDTO> diarieList;

}
