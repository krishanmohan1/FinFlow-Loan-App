package com.capg.lpu.finflow.admin.controller;

import com.capg.lpu.finflow.admin.dto.DecisionRequest;
import com.capg.lpu.finflow.admin.dto.DocumentVerifyRequest;
import com.capg.lpu.finflow.admin.dto.UserUpdateRequest;
import com.capg.lpu.finflow.admin.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for administrative management across the FinFlow microservices architecture.
 * Serves as a centralized orchestration point for managing loans, documents, and user accounts.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    /**
     * Retrieves all loan applications from the application service.
     *
     * @return A response entity containing a list of all loan applications.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Get all loan applications")
    @GetMapping("/loans")
    public ResponseEntity<Object> getAllLoans() {
        log.info("GET /admin/loans");
        return ResponseEntity.ok(adminService.getAllLoans());
    }

    /**
     * Retrieves a specific loan application by its ID.
     *
     * @param id The unique identifier of the loan application.
     * @return A response entity containing the requested loan details.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Get loan by ID")
    @GetMapping("/loans/{id}")
    public ResponseEntity<Object> getLoanById(@PathVariable Long id) {
        log.info("GET /admin/loans/{}", id);
        return ResponseEntity.ok(adminService.getLoanById(id));
    }

    /**
     * Retrieves loan applications filtered by their current status.
     *
     * @param status The status to filter by (e.g., PENDING, APPROVED, REJECTED).
     * @return A response entity containing matching loan applications.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Get loans by status", description = "Status: PENDING, APPROVED, REJECTED, UNDER_REVIEW")
    @GetMapping("/loans/status/{status}")
    public ResponseEntity<Object> getLoansByStatus(@PathVariable String status) {
        log.info("GET /admin/loans/status/{}", status);
        return ResponseEntity.ok(adminService.getLoansByStatus(status));
    }

    /**
     * Retrieves all loan applications submitted by a specific user.
     *
     * @param username The username of the applicant.
     * @return A response entity containing the user's loan applications.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Get loans by username")
    @GetMapping("/loans/user/{username}")
    public ResponseEntity<Object> getLoansByUsername(@PathVariable String username) {
        log.info("GET /admin/loans/user/{}", username);
        return ResponseEntity.ok(adminService.getLoansByUsername(username));
    }

    /**
     * Processes a comprehensive loan decision including interest rate and sanctioned amount.
     *
     * @param id The ID of the loan application.
     * @param request The object containing the decision details and financial parameters.
     * @return A response entity reflecting the updated loan application.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Make a loan decision", description = "Full decision with interest rate, tenure, sanctioned amount")
    @PostMapping("/loans/{id}/decision")
    public ResponseEntity<Object> makeDecision(
            @PathVariable Long id,
            @RequestBody DecisionRequest request) {
        log.info("POST /admin/loans/{}/decision | decision: {}", id, request.getDecision());
        return ResponseEntity.ok(adminService.makeDecision(id, request));
    }

    /**
     * Quickly approves a loan application with standard defaults and optional remarks.
     *
     * @param id The ID of the loan application to approve.
     * @param remarks Administrative comments for the approval.
     * @return A response entity reflecting the approved loan application.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Quick approve a loan")
    @PutMapping("/loans/{id}/approve")
    public ResponseEntity<Object> approveLoan(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Loan approved by admin") String remarks) {
        log.info("PUT /admin/loans/{}/approve", id);
        return ResponseEntity.ok(adminService.approveLoan(id, remarks));
    }

    /**
     * Quickly rejects a loan application with a mandatory reason.
     *
     * @param id The ID of the loan application to reject.
     * @param remarks The reason for rejection.
     * @return A response entity reflecting the rejected loan application.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Quick reject a loan")
    @PutMapping("/loans/{id}/reject")
    public ResponseEntity<Object> rejectLoan(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Loan rejected by admin") String remarks) {
        log.info("PUT /admin/loans/{}/reject", id);
        return ResponseEntity.ok(adminService.rejectLoan(id, remarks));
    }

    /**
     * Marks a loan application as 'UNDER_REVIEW' for further investigation.
     *
     * @param id The ID of the loan application.
     * @return A response entity reflecting the updated status.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Mark loan as under review")
    @PutMapping("/loans/{id}/review")
    public ResponseEntity<Object> markUnderReview(@PathVariable Long id) {
        log.info("PUT /admin/loans/{}/review", id);
        return ResponseEntity.ok(adminService.markUnderReview(id));
    }

    /**
     * Permanently deletes a loan application record.
     *
     * @param id The ID of the loan application to delete.
     * @return A response entity with a success message.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Delete a loan application permanently")
    @DeleteMapping("/loans/{id}")
    public ResponseEntity<String> deleteLoan(@PathVariable Long id) {
        log.info("DELETE /admin/loans/{}", id);
        return ResponseEntity.ok(adminService.deleteLoan(id));
    }

    /**
     * Retrieves all document records uploaded across the system.
     *
     * @return A response entity containing metadata for all documents.
     */
    @Tag(name = "Documents")
    @Operation(summary = "Get all uploaded documents")
    @GetMapping("/documents")
    public ResponseEntity<Object> getAllDocuments() {
        log.info("GET /admin/documents");
        return ResponseEntity.ok(adminService.getAllDocuments());
    }

    /**
     * Retrieves metadata for a specific document by its ID.
     *
     * @param id The unique identifier of the document.
     * @return A response entity containing the document metadata.
     */
    @Tag(name = "Documents")
    @Operation(summary = "Get document by ID")
    @GetMapping("/documents/{id}")
    public ResponseEntity<Object> getDocumentById(@PathVariable Long id) {
        log.info("GET /admin/documents/{}", id);
        return ResponseEntity.ok(adminService.getDocumentById(id));
    }

    /**
     * Retrieves all documents associated with a specific loan application.
     *
     * @param loanId The identifier of the loan.
     * @return A response entity containing a list of related documents.
     */
    @Tag(name = "Documents")
    @Operation(summary = "Get documents by loan ID")
    @GetMapping("/documents/loan/{loanId}")
    public ResponseEntity<Object> getDocumentsByLoanId(@PathVariable String loanId) {
        log.info("GET /admin/documents/loan/{}", loanId);
        return ResponseEntity.ok(adminService.getDocumentsByLoanId(loanId));
    }

    /**
     * Retrieves documents filtered by their verification status.
     *
     * @param status The status to filter by (e.g., PENDING, VERIFIED, REJECTED).
     * @return A response entity containing matching document metadata.
     */
    @Tag(name = "Documents")
    @Operation(summary = "Get documents by verification status", description = "Status: PENDING, VERIFIED, REJECTED")
    @GetMapping("/documents/status/{status}")
    public ResponseEntity<Object> getDocumentsByStatus(@PathVariable String status) {
        log.info("GET /admin/documents/status/{}", status);
        return ResponseEntity.ok(adminService.getDocumentsByStatus(status));
    }

    /**
     * Records a verification decision for a specific document.
     *
     * @param id The ID of the document to verify or reject.
     * @param request The object containing the new status and administrative remarks.
     * @return A response entity reflecting the updated document status.
     */
    @Tag(name = "Documents")
    @Operation(summary = "Verify or reject a document")
    @PutMapping("/documents/{id}/verify")
    public ResponseEntity<Object> verifyDocument(
            @PathVariable Long id,
            @RequestBody DocumentVerifyRequest request) {
        log.info("PUT /admin/documents/{}/verify | status: {}", id, request.getStatus());
        return ResponseEntity.ok(adminService.verifyDocument(id, request));
    }

    /**
     * Permanently deletes a specific document record.
     *
     * @param id The ID of the document to delete.
     * @return A response entity with a success message.
     */
    @Tag(name = "Documents")
    @Operation(summary = "Delete a document permanently")
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        log.info("DELETE /admin/documents/{}", id);
        return ResponseEntity.ok(adminService.deleteDocument(id));
    }

    /**
     * Retrieves a list of all users registered in the system.
     *
     * @return A response entity containing all user profiles.
     */
    @Tag(name = "Users")
    @Operation(summary = "Get all registered users")
    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET /admin/users");
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Retrieves a detailed user profile by its ID.
     *
     * @param id The unique identifier of the user account.
     * @return A response entity containing the user's profile data.
     */
    @Tag(name = "Users")
    @Operation(summary = "Get user by ID")
    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("GET /admin/users/{}", id);
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    /**
     * Updates a user's role or active status.
     *
     * @param id The ID of the user account.
     * @param request The object containing updated role and status information.
     * @return A response entity reflecting the updated user profile.
     */
    @Tag(name = "Users")
    @Operation(summary = "Update user role or active status")
    @PutMapping("/users/{id}")
    public ResponseEntity<Object> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {
        log.info("PUT /admin/users/{}", id);
        return ResponseEntity.ok(adminService.updateUser(id, request));
    }

    /**
     * Deactivates a user account, preventing further system access.
     *
     * @param id The ID of the user account to deactivate.
     * @return A response entity confirming the account deactivation.
     */
    @Tag(name = "Users")
    @Operation(summary = "Deactivate a user account")
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<Object> deactivateUser(@PathVariable Long id) {
        log.info("PUT /admin/users/{}/deactivate", id);
        return ResponseEntity.ok(adminService.deactivateUser(id));
    }

    /**
     * Generates a comprehensive summary report including statistics on loans, documents, and users.
     *
     * @return A response entity containing aggregated platform statistics.
     */
    @Tag(name = "Reports")
    @Operation(summary = "Generate full summary report", description = "Returns loans + documents + users combined")
    @GetMapping("/reports")
    public ResponseEntity<Object> generateReport() {
        log.info("GET /admin/reports");
        return ResponseEntity.ok(adminService.generateReport());
    }

    /**
     * Provides a breakdown of total loan applications grouped by their status.
     *
     * @return A response entity containing loan status distribution counts.
     */
    @Tag(name = "Reports")
    @Operation(summary = "Get loan count grouped by status")
    @GetMapping("/reports/counts")
    public ResponseEntity<Object> getLoanCountByStatus() {
        log.info("GET /admin/reports/counts");
        return ResponseEntity.ok(adminService.getLoanCountByStatus());
    }
}