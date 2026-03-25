package com.capg.lpu.finflow.application.service;

import com.capg.lpu.finflow.application.dto.LoanStatusUpdateRequest;
import com.capg.lpu.finflow.application.entity.LoanApplication;
import com.capg.lpu.finflow.application.exception.ResourceNotFoundException;
import com.capg.lpu.finflow.application.producer.LoanProducer;
import com.capg.lpu.finflow.application.repository.LoanRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final Logger log = LoggerFactory.getLogger(LoanService.class);

    private final LoanRepository loanRepository;
    private final LoanProducer loanProducer;

    // ✅ Apply for a new loan
    public LoanApplication apply(LoanApplication loan) {
        log.info("📝 Applying loan for user: {}", loan.getUsername());

        // status + appliedAt are auto-set via @PrePersist in entity
        LoanApplication saved = loanRepository.save(loan);

        // ✅ Notify Document Service via RabbitMQ
        String message = "NEW_LOAN_APPLICATION | loanId=" + saved.getId()
                + " | username=" + saved.getUsername()
                + " | amount=" + saved.getAmount()
                + " | type=" + saved.getLoanType();

        loanProducer.sendMessage(message);

        log.info("✅ Loan saved with ID: {}", saved.getId());
        return saved;
    }

    // ✅ Get all loans (ADMIN only)
    public List<LoanApplication> getAll() {
        log.info("📋 Fetching all loan applications");
        return loanRepository.findAll();
    }

    // ✅ Get loans by username (USER)
    public List<LoanApplication> getByUsername(String username) {
        log.info("📋 Fetching loans for user: {}", username);
        return loanRepository.findByUsername(username);
    }

    // ✅ Get single loan — secure (USER can only see own, ADMIN can see any)
    public LoanApplication getByIdSecure(Long id, String username, String role) {
        log.info("🔍 Fetching loan ID: {} for user: {}, role: {}", id, username, role);

        LoanApplication loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loan not found with ID: " + id));

        // ✅ If not admin, block access to other user's loans
        if (!"ADMIN".equals(role) && !loan.getUsername().equals(username)) {
            throw new SecurityException("Access denied. This loan does not belong to you.");
        }

        return loan;
    }

    // ✅ Get loans by status (ADMIN only — controller guards this)
    public List<LoanApplication> getByStatus(String status) {
        log.info("📋 Fetching loans with status: {}", status);
        return loanRepository.findByStatus(status);
    }

    // ✅ Get loans by username AND status (USER filtered view)
    public List<LoanApplication> getByUsernameAndStatus(String username, String status) {
        log.info("📋 Fetching loans for user: {} with status: {}", username, status);
        return loanRepository.findByUsernameAndStatus(username, status);
    }

    // ✅ Update loan status (ADMIN only — controller guards this)
    public LoanApplication updateStatus(Long id, LoanStatusUpdateRequest request) {
        log.info("🔄 Updating status for loan ID: {} → {}", id, request.getStatus());

        LoanApplication loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loan not found with ID: " + id));

        loan.setStatus(request.getStatus());
        loan.setRemarks(request.getRemarks());

        LoanApplication updated = loanRepository.save(loan);

        // ✅ Notify Document Service about status change
        String message = "LOAN_STATUS_UPDATED | loanId=" + updated.getId()
                + " | username=" + updated.getUsername()
                + " | newStatus=" + updated.getStatus()
                + " | remarks=" + updated.getRemarks();

        loanProducer.sendMessage(message);

        log.info("✅ Loan ID: {} status updated to: {}", id, request.getStatus());
        return updated;
    }

    // ✅ Delete loan (ADMIN only — controller guards this)
    public String delete(Long id) {
        log.info("🗑️ Deleting loan ID: {}", id);

        LoanApplication loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loan not found with ID: " + id));

        loanRepository.delete(loan);

        log.info("✅ Loan ID: {} deleted successfully", id);
        return "Loan with ID " + id + " deleted successfully.";
    }
}