package com.socia1ca3t.timepillbackup.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户数据统计
 */
@Data
@AllArgsConstructor
public class NotebookStatisticDataVO {

    // 胶囊年龄
    private int age;

    // 日记本数量
    private int notebookNum;

    private int expiredNum;

    private int privateNum;

}
