package com.socia1ca3t.timepillbackup.pojo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class FileDeleteLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath;
    private boolean status;
    private Date deleteEndDate;
    private Date deleteStartDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getDeleteEndDate() {
        return deleteEndDate;
    }

    public void setDeleteEndDate(Date deleteEndDate) {
        this.deleteEndDate = deleteEndDate;
    }

    public Date getDeleteStartDate() {
        return deleteStartDate;
    }

    public void setDeleteStartDate(Date deleteStartDate) {
        this.deleteStartDate = deleteStartDate;
    }
}
