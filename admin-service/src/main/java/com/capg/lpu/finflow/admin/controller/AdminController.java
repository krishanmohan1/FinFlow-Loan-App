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

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    // ================================================================
    // LOAN APPLICATION MANAGEMENT
    // ================================================================

    @Tag(name = "Loans")
    @Operation(summary = "Get all loan applications")
    @GetMapping("/loans")
    public ResponseEntity<Object> getAllLoans() {
        log.info("GET /admin/loans");
        return ResponseEntity.ok(adminService.getAllLoans());
    }

    @Tag(name = "Loans")
    @Operation(summary = "Get loan by ID")
    @GetMapping("/loans/{id}")
    public ResponseEntity<Object> getLoanById(@PathVariable Long id) {
        log.info("GET /admin/loans/{}", id);
        return ResponseEntity.ok(adminService.getLoanById(id));
    }

    @Tag(name = "Loans")
    @Operation(summary = "Get loans by status", description = "Status: PENDING, APPROVED, REJECTED, UNDER_REVIEW")
    @GetMapping("/loans/status/{status}")
    public ResponseEntity<Object> getLoansByStatus(@PathVariable String status) {
        log.info("GET /admin/loans/status/{}", status);
        return ResponseEntity.ok(adminService.getLoansByStatus(status));
    }

    @Tag(name = "Loans")
    @Operation(summary = "Get loans by username")
    @GetMapping("/loans/user/{username}")
    public ResponseEntity<Object> getLoansByUsername(@PathVariable String username) {
        log.info("GET /admin/loans/user/{}", username);
        return ResponseEntity.ok(adminService.getLoansByUsername(username));
    }

    @Tag(name = "Loans")
    @Operation(summary = "Make a loan decision", description = "Full decision with interest rate, tenure, sanctioned amount")
    @PostMapping("/loans/{id}/decision")
    public ResponseEntity<Object> makeDecision(
            @PathVariable Long id,
            @RequestBody DecisionRequest request) {
        log.info("POST /admin/loans/{}/decision | decision: {}", id, request.getDecision());
        return ResponseEntity.ok(adminService.makeDecision(id, request));
    }

    @Tag(name = "Loans")
    @Operation(summary = "Quick approve a loan")
    @PutMapping("/loans/{id}/approve")
    public ResponseEntity<Object> approveLoan(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Loan approved by admin") String remarks) {
        log.info("PUT /admin/loans/{}/approve", id);
        return ResponseEntity.ok(adminService.approveLoan(id, remarks));
    }

    @Tag(name = "Loans")
    @Operation(summary = "Quick reject a loan")
    @PutMapping("/loans/{id}/reject")
    public ResponseEntity<Object> rejectLoan(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Loan rejected by admin") String remarks) {
        log.info("PUT /admin/loans/{}/reject", id);
        return ResponseEntity.ok(adminService.rejectLoan(id, remarks));
    }

    @Tag(name = "Loans")
    @Operation(summary = "Mark loan as under review")
    @PutMapping("/loans/{id}/review")
    public ResponseEntity<Object> markUnderReview(@PathVariable Long id) {
        log.info("PUT /admin/loans/{}/review", id);
        return ResponseEntity.ok(adminService.markUnderReview(id));
    }

    @Tag(name = "Loans")
    @Operation(summary = "Delete a loan application permanently")
    @DeleteMapping("/loans/{id}")
    public ResponseEntity<String> deleteLoan(@PathVariable Long id) {
        log.info("DELETE /admin/loans/{}", id);
        return ResponseEntity.ok(adminService.deleteLoan(id));
    }

    // ================================================================
    // DOCUMENT VERIFICATION
    // ================================================================

    @Tag(name = "Documents")
    @Operation(summary = "Get all uploaded documents")
    @GetMapping("/documents")
    public ResponseEntity<Object> getAllDocuments() {
        log.info("GET /admin/documents");
        return ResponseEntity.ok(adminService.getAllDocuments());
    }

    @Tag(name = "Documents")
    @Operation(summary = "Get document by ID")
    @GetMapping("/documents/{id}")
    public ResponseEntity<Object> getDocumentById(@PathVariable Long id) {
        log.info("GET /admin/documents/{}", id);
        return ResponseEntity.ok(adminService.getDocumentById(id));
    }

    @Tag(name = "Documents")
    @Operation(summary = "Get documents by loan ID")
    @GetMapping("/documents/loan/{loanId}")
    public ResponseEntity<Object> getDocumentsByLoanId(@PathVariable String loanId) {
        log.info("GET /admin/documents/loan/{}", loanId);
        return ResponseEntity.ok(adminService.getDocumentsByLoanId(loanId));
    }

    @Tag(name = "Documents")
    @Operation(summary = "Get documents by verification status", description = "Status: PENDING, VERIFIED, REJECTED")
    @GetMapping("/documents/status/{status}")
    public ResponseEntity<Object> getDocumentsByStatus(@PathVariable String status) {
        log.info("GET /admin/documents/status/{}", status);
        return ResponseEntity.ok(adminService.getDocumentsByStatus(status));
    }

    @Tag(name = "Documents")
    @Operation(summary = "Verify or reject a document")
    @PutMapping("/documents/{id}/verify")
    public ResponseEntity<Object> verifyDocument(
            @PathVariable Long id,
            @RequestBody DocumentVerifyRequest request) {
        log.info("PUT /admin/documents/{}/verify | status: {}", id, request.getStatus());
        return ResponseEntity.ok(adminService.verifyDocument(id, request));
    }

    @Tag(name = "Documents")
    @Operation(summary = "Delete a document permanently")
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        log.info("DELETE /admin/documents/{}", id);
        return ResponseEntity.ok(adminService.deleteDocument(id));
    }

    // ================================================================
    // USER MANAGEMENT
    // ================================================================

    @Tag(name = "Users")
    @Operation(summary = "Get all registered users")
    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET /admin/users");
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @Tag(name = "Users")
    @Operation(summary = "Get user by ID")
    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("GET /admin/users/{}", id);
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @Tag(name = "Users")
    @Operation(summary = "Update user role or active status")
    @PutMapping("/users/{id}")
    public ResponseEntity<Object> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {
        log.info("PUT /admin/users/{}", id);
        return ResponseEntity.ok(adminService.updateUser(id, request));
    }

    @Tag(name = "Users")
    @Operation(summary = "Deactivate a user account")
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<Object> deactivateUser(@PathVariable Long id) {
        log.info("PUT /admin/users/{}/deactivate", id);
        return ResponseEntity.ok(adminService.deactivateUser(id));
    }

    // ================================================================
    // REPORTS
    // ================================================================

    @Tag(name = "Reports")
    @Operation(summary = "Generate full summary report", description = "Returns loans + documents + users combined")
    @GetMapping("/reports")
    public ResponseEntity<Object> generateReport() {
        log.info("GET /admin/reports");
        return ResponseEntity.ok(adminService.generateReport());
    }

    @Tag(name = "Reports")
    @Operation(summary = "Get loan count grouped by status")
    @GetMapping("/reports/counts")
    public ResponseEntity<Object> getLoanCountByStatus() {
        log.info("GET /admin/reports/counts");
        return ResponseEntity.ok(adminService.getLoanCountByStatus());
    }
}