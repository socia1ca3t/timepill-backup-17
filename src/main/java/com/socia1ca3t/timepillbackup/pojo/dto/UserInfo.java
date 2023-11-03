package com.socia1ca3t.timepillbackup.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/*
    用户个人信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo implements Serializable {

    private int id;

    private String email;

    private String name;

    private String intro;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-mm-dd")
    private String created;

    private String state;

    private String iconUrl;

    private String coverUrl;

    /**
     * 头像转换为本地资源后的路径
     */
    private String iconImgSrc;

    public String getIconImgSrc() {
        return iconImgSrc;
    }

    public void setIconImgSrc(String iconImgSrc) {
        this.iconImgSrc = iconImgSrc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
