package com.finflow.application.repository;

import com.finflow.application.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanApplication, Long> {

    List<LoanApplication> findByUsername(String username);

    List<LoanApplication> findByStatus(String status);
}