package com.capg.lpu.finflow.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capg.lpu.finflow.application.entity.LoanApplication;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanApplication, Long> {

    // Get all loans by username
    List<LoanApplication> findByUsername(String username);

    // Get all loans by status
    List<LoanApplication> findByStatus(String status);

    // Get loans by username AND status
    List<LoanApplication> findByUsernameAndStatus(String username, String status);
}