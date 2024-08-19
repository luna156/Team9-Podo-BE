package com.softeer.podo.log.repository;

import com.softeer.podo.log.model.entity.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {
}
