package com.socia1ca3t.timepillbackup.pojo.entity;

import com.socia1ca3t.timepillbackup.core.Backuper;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class PrepareFilesLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String prepareCode;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    private Backuper.Type backupType;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private Date startDate;

    private Date completeDate;

    private Date exceptionDate;

    private String exceptionMsg;

    private Long minutesSpend;

    private String userEvaluation;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrepareCode() {
        return prepareCode;
    }

    public void setPrepareCode(String prepareCode) {
        this.prepareCode = prepareCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Backuper.Type getBackupType() {
        return backupType;
    }

    public void setBackupType(Backuper.Type backupType) {
        this.backupType = backupType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    public Date getExceptionDate() {
        return exceptionDate;
    }

    public void setExceptionDate(Date exceptionDate) {
        this.exceptionDate = exceptionDate;
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }

    public Long getMinutesSpend() {
        return minutesSpend;
    }

    public void setMinutesSpend(Long minutesSpend) {
        this.minutesSpend = minutesSpend;
    }

    public String getUserEvaluation() {
        return userEvaluation;
    }

    public void setUserEvaluation(String userEvaluation) {
        this.userEvaluation = userEvaluation;
    }
}
