package com.socia1ca3t.timepillbackup.pojo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.socia1ca3t.timepillbackup.pojo.api.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO extends User implements Serializable {

    private String email;

    /**
     * 头像转换为本地资源后的路径
     */
    private String iconImgSrc;

}
