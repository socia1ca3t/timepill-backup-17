package com.socia1ca3t.timepillbackup.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
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

}
