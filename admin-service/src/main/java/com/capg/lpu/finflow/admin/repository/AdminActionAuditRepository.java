package com.capg.lpu.finflow.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capg.lpu.finflow.admin.entity.AdminActionAudit;

/**
 * Repository for admin action audit records.
 */
@Repository
public interface AdminActionAuditRepository extends JpaRepository<AdminActionAudit, Long> {

    List<AdminActionAudit> findTop20ByOrderByCreatedAtDesc();
}
