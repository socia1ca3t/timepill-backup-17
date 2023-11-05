package com.socia1ca3t.timepillbackup.pojo.vo;

import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;
import com.socia1ca3t.timepillbackup.pojo.dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户个人主页
 */
@Data
@AllArgsConstructor
public class HomePageVO implements Serializable {

    private UserInfo userInfo;

    private NotebookStatisticDataVO userData;

    private List<NoteBook> myNotebooks;

}
