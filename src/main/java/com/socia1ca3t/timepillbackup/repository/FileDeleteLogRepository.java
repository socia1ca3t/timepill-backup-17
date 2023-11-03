package com.socia1ca3t.timepillbackup.repository;

import com.socia1ca3t.timepillbackup.pojo.entity.FileDeleteLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDeleteLogRepository extends JpaRepository<FileDeleteLog, Long> {
}
