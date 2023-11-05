package com.socia1ca3t.timepillbackup.pojo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.socia1ca3t.timepillbackup.pojo.vo.DiariesStatisticData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiariesDTO extends DiariesStatisticData implements Serializable {

    /**
     * 第几页
     */
    private int page;

    /**
     * 每页日记的篇数
     */
    @JsonProperty("page_size")
    private int pageSize;

    /**
     * 日记总数
     */
    private int count;

    private List<Diary> items;
}
