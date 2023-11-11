package com.socia1ca3t.timepillbackup.pojo.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    private int id;

    private String name;

    private String intro;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-mm-dd")
    private String created;

    private String state;

    private String iconUrl;

    private String coverUrl;

}
