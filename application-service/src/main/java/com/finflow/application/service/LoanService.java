package com.finflow.application.service;

import com.finflow.application.dto.LoanStatusUpdateRequest;
import com.finflow.application.entity.LoanApplication;
import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.application.producer.LoanProducer;
import com.finflow.application.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final Logger log = LoggerFactory.getLogger(LoanService.class);

    private final LoanRepository loanRepository;
    private final LoanProducer loanProducer;

    // ✅ Apply for a loan
    public LoanApplication apply(LoanApplication loan) {
        log.info("📥 New loan application → user: {}, amount: {}, type: {}",
                loan.getUsername(), loan.getAmount(), loan.getLoanType());

        loan.setStatus("PENDING");
        loan.setAppliedAt(LocalDateTime.now());

        LoanApplication saved = loanRepository.save(loan);

        String message = String.format(
            "LOAN_APPLIED | id=%d | user=%s | amount=%.2f | type=%s | purpose=%s",
            saved.getId(), saved.getUsername(), saved.getAmount(),
            saved.getLoanType(), saved.getPurpose()
        );
        loanProducer.sendMessage(message);

        log.info("✅ Loan saved → ID: {}", saved.getId());
        return saved;
    }

    // ✅ Get all loans
    public List<LoanApplication> getAll() {
        log.info("📋 Fetching all loan applications");
        return loanRepository.findAll();
    }

    // ✅ Get by ID
    public LoanApplication getById(Long id) {
        log.info("🔍 Fetching loan by ID: {}", id);
        return loanRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("❌ Loan not found with ID: {}", id);
                    return new ResourceNotFoundException("Loan not found with ID: " + id);
                });
    }

    // ✅ Get by username
    public List<LoanApplication> getByUsername(String username) {
        log.info("👤 Fetching loans for user: {}", username);
        return loanRepository.findByUsername(username);
    }

    // ✅ Get by status
    public List<LoanApplication> getByStatus(String status) {
        log.info("🔎 Fetching loans with status: {}", status);
        return loanRepository.findByStatus(status);
    }

    // ✅ Update status with remarks — called by Admin Service via Feign
    public LoanApplication updateStatus(Long id, LoanStatusUpdateRequest request) {
        log.info("🔄 Updating loan ID: {} → status: {}, remarks: {}",
                id, request.getStatus(), request.getRemarks());

        LoanApplication loan = getById(id);
        loan.setStatus(request.getStatus());
        loan.setRemarks(request.getRemarks());

        LoanApplication updated = loanRepository.save(loan);

        String message = String.format(
            "LOAN_STATUS_UPDATED | id=%d | user=%s | status=%s | remarks=%s",
            updated.getId(), updated.getUsername(),
            updated.getStatus(), updated.getRemarks()
        );
        loanProducer.sendMessage(message);

        log.info("✅ Loan ID: {} status updated to: {}", id, request.getStatus());
        return updated;
    }

    // ✅ Delete
    public String delete(Long id) {
        log.info("🗑️ Deleting loan with ID: {}", id);
        LoanApplication loan = getById(id);
        loanRepository.delete(loan);
        log.info("✅ Loan ID: {} deleted", id);
        return "Loan application deleted successfully";
    }
}