package com.socia1ca3t.timepillbackup.pojo.vo;

import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NotebooksOfOneYear {

    private int year;

    private List<NoteBook> noteBooks;

}
