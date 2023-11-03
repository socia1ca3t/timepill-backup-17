package com.socia1ca3t.timepillbackup.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/*
    日记
 */
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

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getNotebookId() {
        return notebookId;
    }

    public void setNotebookId(int notebookId) {
        this.notebookId = notebookId;
    }

    public String getNotebookName() {
        return notebookName;
    }

    public void setNotebookName(String notebookName) {
        this.notebookName = notebookName;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getContentImgURL() {
        return contentImgURL;
    }

    public void setContentImgURL(String contentImgURL) {
        this.contentImgURL = contentImgURL;
    }

    public String getContentImgThumbURL() {
        return contentImgThumbURL;
    }

    public void setContentImgThumbURL(String contentImgThumbURL) {
        this.contentImgThumbURL = contentImgThumbURL;
    }
}
