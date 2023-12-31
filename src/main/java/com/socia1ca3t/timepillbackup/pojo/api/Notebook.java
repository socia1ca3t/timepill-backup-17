package com.socia1ca3t.timepillbackup.pojo.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notebook implements Serializable {

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

    // 是否有封面 0 无 1 有
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


}
