package com.socia1ca3t.timepillbackup.pojo.api;

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

    private boolean liked;

    @JsonProperty("like_count")
    private int likeCount;

    private int type;

    @JsonProperty("comment_count")
    private int commentCount;

}
