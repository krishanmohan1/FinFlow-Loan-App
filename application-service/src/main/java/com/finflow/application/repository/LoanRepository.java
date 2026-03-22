package com.finflow.application.repository;

import com.finflow.application.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<LoanApplication, Long> {
}