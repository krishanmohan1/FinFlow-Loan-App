package com.capg.lpu.finflow.application.service;

import com.capg.lpu.finflow.application.dto.LoanStatusUpdateRequest;
import com.capg.lpu.finflow.application.dto.LoanEventMessage;
import com.capg.lpu.finflow.application.entity.LoanApplication;
import com.capg.lpu.finflow.application.exception.ResourceNotFoundException;
import com.capg.lpu.finflow.application.producer.LoanProducer;
import com.capg.lpu.finflow.application.repository.LoanRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing loan application lifecycles.
 * Handles business logic for applications, status updates, and coordination with the message producer.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanProducer loanProducer;

    /**
     * Processes and persists a new loan application.
     * Triggers a notification to other microservices via RabbitMQ upon successful creation.
     *
     * @param loan The unpersisted loan application entity.
     * @return The saved loan application with generated ID and metadata.
     */
    public LoanApplication apply(LoanApplication loan) {
        log.info("Applying loan for user: {}", loan.getUsername());

        LoanApplication saved = loanRepository.save(loan);
        loanProducer.sendLoanCreated(LoanEventMessage.builder()
                .eventType("NEW_LOAN_APPLICATION")
                .loanId(saved.getId())
                .username(saved.getUsername())
                .amount(saved.getAmount())
                .loanType(saved.getLoanType())
                .status(saved.getStatus())
                .occurredAt(LocalDateTime.now())
                .build());

        log.info("Loan saved with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Withdraws a borrower's own pending or under-review application.
     *
     * @param id The application identifier.
     * @param username The authenticated borrower username.
     * @return The updated application in withdrawn state.
     */
    public LoanApplication withdraw(Long id, String username) {
        log.info("Withdrawing loan ID: {} for user: {}", id, username);

        LoanApplication loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + id));

        if (!loan.getUsername().equals(username)) {
            throw new SecurityException("Access denied. This loan does not belong to you.");
        }

        if (!"PENDING".equals(loan.getStatus()) && !"UNDER_REVIEW".equals(loan.getStatus())) {
            throw new IllegalStateException("Only PENDING or UNDER_REVIEW loans can be withdrawn");
        }

        loan.setStatus("WITHDRAWN");
        loan.setRemarks("Application withdrawn by borrower");

        LoanApplication updated = loanRepository.save(loan);
        loanProducer.sendLoanStatusUpdated(LoanEventMessage.builder()
                .eventType("LOAN_STATUS_UPDATED")
                .loanId(updated.getId())
                .username(updated.getUsername())
                .status(updated.getStatus())
                .remarks(updated.getRemarks())
                .occurredAt(LocalDateTime.now())
                .build());
        return updated;
    }

    /**
     * Retrieves all loan applications from the system.
     *
     * @return A list of all stored loan application records.
     */
    public List<LoanApplication> getAll() {
        log.info("Fetching all loan applications");
        return loanRepository.findAll();
    }

    /**
     * Retrieves all loan applications submitted by a specific user.
     *
     * @param username The username to filter by.
     * @return A list of applications matching the username.
     */
    public List<LoanApplication> getByUsername(String username) {
        log.info("Fetching loans for user: {}", username);
        return loanRepository.findByUsername(username);
    }

    /**
     * Retrieves a loan application by ID while enforcing security constraints.
     * Standard users can only access their own records, while ADMINs have unrestricted access.
     *
     * @param id The ID of the loan application.
     * @param username The name of the authenticated requester.
     * @param role The security role of the requester.
     * @return The requested loan application.
     * @throws ResourceNotFoundException if the application ID does not exist.
     * @throws SecurityException if the user is not authorized to view the application.
     */
    public LoanApplication getByIdSecure(Long id, String username, String role) {
        log.info("Fetching loan ID: {} for user: {}, role: {}", id, username, role);

        LoanApplication loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loan not found with ID: " + id));

        if (!"ADMIN".equals(role) && !loan.getUsername().equals(username)) {
            throw new SecurityException("Access denied. This loan does not belong to you.");
        }

        return loan;
    }

    /**
     * Retrieves all loan applications filtered by their current status.
     *
     * @param status The target status for matches.
     * @return A list of applications with the matching status.
     */
    public List<LoanApplication> getByStatus(String status) {
        log.info("Fetching loans with status: {}", status);
        return loanRepository.findByStatus(status);
    }

    /**
     * Retrieves loan applications matching both a specific username and status.
     *
     * @param username The target applicant's username.
     * @param status The target application status.
     * @return A list of applications satisfying both criteria.
     */
    public List<LoanApplication> getByUsernameAndStatus(String username, String status) {
        log.info("Fetching loans for user: {} with status: {}", username, status);
        return loanRepository.findByUsernameAndStatus(username, status);
    }

    /**
     * Updates the status and metadata of an existing loan application.
     * Dispatches an update notification to environmental services via the message producer.
     *
     * @param id The ID of the application to update.
     * @param request The status update details (new status and remarks).
     * @return The updated loan application entity.
     * @throws ResourceNotFoundException if the ID corresponds to no known application.
     */
    public LoanApplication updateStatus(Long id, LoanStatusUpdateRequest request) {
        log.info("Updating status for loan ID: {} - {}", id, request.getStatus());

        LoanApplication loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loan not found with ID: " + id));

        loan.setStatus(request.getStatus());
        loan.setRemarks(request.getRemarks());

        LoanApplication updated = loanRepository.save(loan);
        loanProducer.sendLoanStatusUpdated(LoanEventMessage.builder()
                .eventType("LOAN_STATUS_UPDATED")
                .loanId(updated.getId())
                .username(updated.getUsername())
                .status(updated.getStatus())
                .remarks(updated.getRemarks())
                .occurredAt(LocalDateTime.now())
                .build());

        log.info("Loan ID: {} status updated to: {}", id, request.getStatus());
        return updated;
    }

    /**
     * Deletes a loan application record from the persistence layer.
     *
     * @param id The ID of the application to remove.
     * @return A confirmation string upon successful deletion.
     * @throws ResourceNotFoundException if the application ID is not found.
     */
    public String delete(Long id) {
        log.info("Deleting loan ID: {}", id);

        LoanApplication loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loan not found with ID: " + id));

        loanRepository.delete(loan);

        log.info("Loan ID: {} deleted successfully", id);
        return "Loan with ID " + id + " deleted successfully.";
    }
}
