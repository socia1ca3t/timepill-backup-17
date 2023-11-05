package com.socia1ca3t.timepillbackup.pojo.entity;

import com.socia1ca3t.timepillbackup.core.Backuper;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
@Data
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

}
