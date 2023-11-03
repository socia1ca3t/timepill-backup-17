package com.socia1ca3t.timepillbackup.repository;

import com.socia1ca3t.timepillbackup.pojo.entity.PrepareFilesLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrepareFilesLogRepository extends JpaRepository<PrepareFilesLog, Long> {


    @Query(value = "SELECT e FROM PrepareFilesLog e WHERE e.prepareCode = :prepareCode AND e.completeDate IS NULL AND e.exceptionDate IS NULL")
    List<PrepareFilesLog> findByCompleteDateAndExceptionDateIsNull(String prepareCode);
}
