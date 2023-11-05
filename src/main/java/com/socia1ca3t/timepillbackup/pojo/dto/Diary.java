package com.socia1ca3t.timepillbackup.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Diary implements Serializable {

    private int id;

    @JsonProperty("user_id")
    private int userId;

    // 头像链接
    private String iconURL;

    // 昵称
    private String nickName;

    // 日记本名
    @JsonProperty("notebook_id")
    private int notebookId;

    // 日记本名
    @JsonProperty("notebook_subject")
    private String notebookName;

    // 内容文本
    @JsonProperty("content")
    private String contentText;

    @JsonProperty("created")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String postTime;

    @JsonProperty("updated")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String updatedDate;

    // 内容图片
    @JsonProperty("photoUrl")
    private String contentImgURL;

    // 内容图片缩略图
    @JsonProperty("photoThumbUrl")
    private String contentImgThumbURL;

    // 内容原片转换为本地资源后的路径
    private String imgSrc;

}
