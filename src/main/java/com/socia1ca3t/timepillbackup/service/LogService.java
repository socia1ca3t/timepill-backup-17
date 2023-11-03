package com.socia1ca3t.timepillbackup.service;

import com.socia1ca3t.timepillbackup.core.Backuper;
import com.socia1ca3t.timepillbackup.pojo.entity.FileDeleteLog;
import com.socia1ca3t.timepillbackup.pojo.entity.PrepareFilesLog;
import com.socia1ca3t.timepillbackup.repository.FileDeleteLogRepository;
import com.socia1ca3t.timepillbackup.repository.PrepareFilesLogRepository;
import com.socia1ca3t.timepillbackup.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LogService {

    @Autowired
    private PrepareFilesLogRepository prepareFilesRepo;


    @Autowired
    private FileDeleteLogRepository fileDeleteLogRepo;

    public PrepareFilesLog insertPrepareFilesLog(String username, Date startDate, Date completeDate, Backuper.Type backupType, String prepareCode, String fileName) {

        PrepareFilesLog log = new PrepareFilesLog();

        log.setStartDate(startDate);
        log.setPrepareCode(prepareCode);
        log.setFileName(fileName);
        log.setUsername(username);
        log.setBackupType(backupType);
        log.setStartDate(new Date());

        if (startDate != null && completeDate != null) {
            log.setMinutesSpend(DateUtil.minutesBetweenDate(startDate, completeDate));
        } else {
            log.setMinutesSpend(0L);
        }

        try {
            prepareFilesRepo.saveAndFlush(log);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return log;
    }


    public PrepareFilesLog updateUserEvalutionOfPrepareFilesLog(Long logId, String userEvalution) {


        PrepareFilesLog prepareFilesLog = prepareFilesRepo.findById(logId).orElse(null);
        if (prepareFilesLog != null) {

            prepareFilesLog.setUserEvaluation(userEvalution);
            return prepareFilesRepo.saveAndFlush(prepareFilesLog);
        }
        return prepareFilesLog;
    }


    public PrepareFilesLog endPrepareFilesLogForException(Long logId, String exMsg) {

        PrepareFilesLog prepareFilesLog = prepareFilesRepo.findById(logId).orElse(null);
        if (prepareFilesLog != null) {

            prepareFilesLog.setExceptionMsg(exMsg);
            prepareFilesLog.setCompleteDate(new Date());
            prepareFilesRepo.saveAndFlush(prepareFilesLog);
        }

        return prepareFilesLog;
    }


    public PrepareFilesLog updateExceptionDateOfPrepareFilesLog(PrepareFilesLog log, Date exDate, String exMsg) {

        log.setExceptionDate(exDate);
        log.setExceptionMsg(exMsg);
        try {
            prepareFilesRepo.saveAndFlush(log);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return log;
    }

    public PrepareFilesLog updateCompeleDateOfPrepareFilesLog(PrepareFilesLog log, Date endDate) {

        log.setCompleteDate(endDate);
        log.setMinutesSpend(DateUtil.minutesBetweenDate(log.getStartDate(), endDate));

        try {
            prepareFilesRepo.saveAndFlush(log);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return log;
    }


    public FileDeleteLog insertFileDeleteLog(boolean status, String fileName, Date startDate, Date endDate) {

        FileDeleteLog log = new FileDeleteLog();
        log.setStatus(status);
        log.setFilePath(fileName);
        log.setDeleteEndDate(endDate);
        log.setDeleteStartDate(startDate);

        try {
            fileDeleteLogRepo.save(log);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return log;
    }

}
