package com.capg.lpu.finflow.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capg.lpu.finflow.admin.entity.ReportSnapshot;

/**
 * Repository for generated report snapshots.
 */
@Repository
public interface ReportSnapshotRepository extends JpaRepository<ReportSnapshot, Long> {

    List<ReportSnapshot> findTop10ByOrderByGeneratedAtDesc();
}
