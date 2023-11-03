package com.socia1ca3t.timepillbackup.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/*
    日记本
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoteBook implements Serializable {

    private int id;

    @JsonProperty("subject")
    private String name;

    @JsonProperty("user_id")
    private int userId;

    private String description;
    private int privacy;

    @JsonProperty("isExpired")
    private boolean hasExpired;

    @JsonProperty("isPublic")
    private boolean toPublic;

    private int cover;

    @JsonProperty("coverUrl")
    private String coverImgURL;

    @JsonProperty("created")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private String createDate;

    @JsonProperty("expired")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private String expireDate;

    @JsonProperty("updated")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private String updatedDate;

    // 将封面图片转换为本资源后的路径
    private String coverImgSrc;

    public String getCoverImgSrc() {
        return coverImgSrc;
    }

    public void setCoverImgSrc(String coverImgSrc) {
        this.coverImgSrc = coverImgSrc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

    public boolean isHasExpired() {
        return hasExpired;
    }

    public void setHasExpired(boolean hasExpired) {
        this.hasExpired = hasExpired;
    }

    public boolean isToPublic() {
        return toPublic;
    }

    public void setToPublic(boolean toPublic) {
        this.toPublic = toPublic;
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }

    public String getCoverImgURL() {
        return coverImgURL;
    }

    public void setCoverImgURL(String coverImgURL) {
        this.coverImgURL = coverImgURL;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }
}
