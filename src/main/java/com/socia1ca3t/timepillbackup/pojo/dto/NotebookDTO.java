package com.socia1ca3t.timepillbackup.pojo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.socia1ca3t.timepillbackup.pojo.api.Notebook;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotebookDTO extends Notebook implements Serializable {

    // 将封面图片转换为本资源后的路径
    private String coverImgSrc;

}
