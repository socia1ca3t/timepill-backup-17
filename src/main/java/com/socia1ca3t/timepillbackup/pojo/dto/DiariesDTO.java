package com.socia1ca3t.timepillbackup.pojo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.socia1ca3t.timepillbackup.pojo.vo.DiariesStatisticData;

import java.io.Serializable;
import java.util.List;

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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Diary> getItems() {
        return items;
    }

    public void setItems(List<Diary> items) {
        this.items = items;
    }
}
