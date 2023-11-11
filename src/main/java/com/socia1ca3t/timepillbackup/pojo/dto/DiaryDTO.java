package com.socia1ca3t.timepillbackup.pojo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.socia1ca3t.timepillbackup.pojo.api.Diary;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;



@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiaryDTO extends Diary implements Serializable {


    // 内容原片转换为本地资源后的路径
    private String imgSrc;

}
