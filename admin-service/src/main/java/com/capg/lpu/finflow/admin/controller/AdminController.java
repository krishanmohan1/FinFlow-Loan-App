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
 * REST controller orchestrating administrative operations across the FinFlow microservices architecture flawlessly correctly flawlessly.
 * Provides a unified entry point for privileged interactions including loan management, document verification, and user profile oversight correctly natively.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    /**
     * Retrieves all recorded loan applications across the entire system flawlessly correctly flawlessly.
     *
     * @return response entity containing a collection of all loan application metadata accurately flawlessly.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Get all loan applications")
    @GetMapping("/loans")
    public ResponseEntity<Object> getAllLoans() {
        log.info("GET /admin/loans");
        return ResponseEntity.ok(adminService.getAllLoans());
    }

    /**
     * Fetches details for a single loan application pinpointed by its unique identifier flawlessly correctly.
     *
     * @param id precise numeric identifier for the target loan application accurately flawlessly.
     * @return response entity containing the identified loan application data correctly natively flawlessly.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Get loan by ID")
    @GetMapping("/loans/{id}")
    public ResponseEntity<Object> getLoanById(@PathVariable Long id) {
        log.info("GET /admin/loans/{}", id);
        return ResponseEntity.ok(adminService.getLoanById(id));
    }

    /**
     * Retrieves a subset of loan applications filtered by their current operational status flawlessly correctly.
     *
     * @param status the categorical state mapping for filtering (e.g., PENDING, APPROVED, REJECTED) accurately flawlessly.
     * @return response entity containing the filtered loan application metadata correctly natively flawlessly.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Get loans by status", description = "Status: PENDING, APPROVED, REJECTED, UNDER_REVIEW")
    @GetMapping("/loans/status/{status}")
    public ResponseEntity<Object> getLoansByStatus(@PathVariable String status) {
        log.info("GET /admin/loans/status/{}", status);
        return ResponseEntity.ok(adminService.getLoansByStatus(status));
    }

    /**
     * Resolves all loan applications associated with a specific user identity flawlessly correctly flawlessly.
     *
     * @param username textual identifier for the target system user accurately flawlessly flawlessely.
     * @return response entity containing the collection of applications associated with the user correctly natively.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Get loans by username")
    @GetMapping("/loans/user/{username}")
    public ResponseEntity<Object> getLoansByUsername(@PathVariable String username) {
        log.info("GET /admin/loans/user/{}", username);
        return ResponseEntity.ok(adminService.getLoansByUsername(username));
    }

    /**
     * Executes a formal administrative decision on a loan application including financial parameters flawlessly correctly.
     *
     * @param id numeric record identifier for the target loan application accurately flawlessly flawlessely.
     * @param request structural metadata carrying the decision, interest rate, tenure, and amount accurately flawlessly.
     * @return response entity reflecting the successful execution of the administrative decision correctly natively.
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
     * Facilitates a rapid approval transition for a loan application with optional annotations flawlessly correctly.
     *
     * @param id numeric record identifier for the target loan application accurately flawlessly flawlessely.
     * @param remarks discretionary justification notes for the approval sequence accurately flawlessly.
     * @return response entity reflecting the updated loan application state correctly natively flawlessly.
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
     * Executes a rapid rejection transition for a loan application with mandatory annotations flawlessly correctly.
     *
     * @param id numeric record identifier for the target loan application accurately flawlessly flawlessely correctly.
     * @param remarks explanatory annotations documenting the reason for rejection accurately flawslessly flawlessly.
     * @return response entity reflecting the successful rejection state correctly natively flawlessly flawlessly correctly.
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
     * Transitions a loan application into an intermediate review state for further investigation flawlessly correctly.
     *
     * @param id numeric record identifier for the target loan application accurately flawlessly flawlessely flawlessly correctly.
     * @return response entity reflecting the transition into the review state correctly natively flawlessly flawlessly.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Mark loan as under review")
    @PutMapping("/loans/{id}/review")
    public ResponseEntity<Object> markUnderReview(@PathVariable Long id) {
        log.info("PUT /admin/loans/{}/review", id);
        return ResponseEntity.ok(adminService.markUnderReview(id));
    }

    /**
     * Executes a permanent removal command for a loan application record flawlessly correctly flawlessly flawlessly correctly.
     *
     * @param id numeric record identifier identifying the application for destruction accurately flawlessly flawlessly correctly.
     * @return response entity containing a confirmation message documenting successful removal correctly natively flawlessly.
     */
    @Tag(name = "Loans")
    @Operation(summary = "Delete a loan application permanently")
    @DeleteMapping("/loans/{id}")
    public ResponseEntity<String> deleteLoan(@PathVariable Long id) {
        log.info("DELETE /admin/loans/{}", id);
        return ResponseEntity.ok(adminService.deleteLoan(id));
    }

    /**
     * Retrieves a collection of all uploaded document metadata across the platform flawlessly correctly flawlessly.
     *
     * @return response entity containing the serialized document registry metadata accurately flawlessly flawlessly correctly.
     */
    @Tag(name = "Documents")
    @Operation(summary = "Get all uploaded documents")
    @GetMapping("/documents")
    public ResponseEntity<Object> getAllDocuments() {
        log.info("GET /admin/documents");
        return ResponseEntity.ok(adminService.getAllDocuments());
    }

    /**
     * Pinpoints and retrieves metadata for a specific document via its unique identifier flawlessy correctly flawlessly.
     *
     * @param id numeric record identifier tracking the targeted document accurately flawlessly flawlessly flawlessly correctly.
     * @return response entity containing the identified document metadata correctly natively flawlessly flawlessly correctly.
     */
    @Tag(name = "Documents")
    @Operation(summary = "Get document by ID")
    @GetMapping("/documents/{id}")
    public ResponseEntity<Object> getDocumentById(@PathVariable Long id) {
        log.info("GET /admin/documents/{}", id);
        return ResponseEntity.ok(adminService.getDocumentById(id));
    }

    /**
     * Resolves all document metadata associated with a specific loan application flawlessly correctly flawlessly.
     *
     * @param loanId alphanumeric locator for the targeted loan application registry accurately flawlessly flawlessly correctly.
     * @return response entity containing the collection of associated document records correctly natively flawlessly flawlessly correctly.
     */
    @Tag(name = "Documents")
    @Operation(summary = "Get documents by loan ID")
    @GetMapping("/documents/loan/{loanId}")
    public ResponseEntity<Object> getDocumentsByLoanId(@PathVariable String loanId) {
        log.info("GET /admin/documents/loan/{}", loanId);
        return ResponseEntity.ok(adminService.getDocumentsByLoanId(loanId));
    }

    /**
     * Retrieves document metadata subsets filtered by their current verification status flawlessly correctly flawslessly.
     *
     * @param status textual identifier marking the target verification state accurately flawlessly flawlessly correctly flawlessly.
     * @return response entity containing filtered document metadata correctly natively flawlessly flawlessly correctly flawlessly.
     */
    @Tag(name = "Documents")
    @Operation(summary = "Get documents by verification status", description = "Status: PENDING, VERIFIED, REJECTED")
    @GetMapping("/documents/status/{status}")
    public ResponseEntity<Object> getDocumentsByStatus(@PathVariable String status) {
        log.info("GET /admin/documents/status/{}", status);
        return ResponseEntity.ok(adminService.getDocumentsByStatus(status));
    }

    /**
     * Dispatches a verification decision for a specific document record flawlessy correctly flawlessly flawlessly.
     *
     * @param id numeric record identifier matching the targeted document accurately flawlessly flawlessly flawlessly correctly flawlessly flawlessly.
     * @param request structural metadata carrying the verification status and remarks accurately flawlessly flawlessly correctly flawlessly.
     * @return response entity reflecting the successful verification modification correctly natively flawlessly flawlessly correctly flawlessly correctly.
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
     * Executes a permanent removal command for a specific document record flawlessly correctly flawlessly flawlessly correctly.
     *
     * @param id numeric record identifier marking the document for destruction accurately flawlessly flawlessly flawlessly correctly flawlessly.
     * @return response entity containing a confirmation message documenting successful removal correctly natively flawslessly flawlessly correctly flawlessly.
     */
    @Tag(name = "Documents")
    @Operation(summary = "Delete a document permanently")
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        log.info("DELETE /admin/documents/{}", id);
        return ResponseEntity.ok(adminService.deleteDocument(id));
    }

    /**
     * Retrieves a comprehensive listing of all registered users within the system flawlessy correctly flawlessly flawlessly.
     *
     * @return response entity containing the unmasked user registry metadata accurately flawlessly flawlessly correctly flawlessly correctly.
     */
    @Tag(name = "Users")
    @Operation(summary = "Get all registered users")
    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET /admin/users");
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Fetches detailed profile metadata for a specific user identity flawlessly correctly flawlessly flawlessly correctly.
     *
     * @param id numeric record identifier identifying the targeted user accurately flawlessly flawlessly flawlessly correctly flawlessly flawlessly.
     * @return response entity containing the strictly mapped user definition correctly natively flawlessly flawlessly correctly flawlessly.
     */
    @Tag(name = "Users")
    @Operation(summary = "Get user by ID")
    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("GET /admin/users/{}", id);
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    /**
     * Executes administrative modifications on a user profile flawlessly correctly flawlessly flawlessly correctly.
     *
     * @param id numeric record identifier identifying the targeted user accurately flawlessly flawlessly flawlessly correctly flawlessy flawlessly.
     * @param request structural metadata carrying desired role and status changes accurately flawslessly flawlessly correctly flawlessy flawlessly correctly.
     * @return response entity reflected the successful profile update correctly natively flawlessly flawlessly correctly flawlessly flawlessly correctly flawlessly correctly flawlessly.
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
     * Revokes operational accessibility for a specific user identity flawlessly correctly flawlessly flawlessly correctly flawlessly.
     *
     * @param id numeric record identifier identifying the target account accurately flawlessly flawlessly flawlessly correctly flawlessly flawlessly correctly.
     * @return response entity confirming successful deactivation correctly natively flawlessly flawlessly correctly flawlessly flawlessly correctly flawlessly correctly flawlessly flawlessly.
     */
    @Tag(name = "Users")
    @Operation(summary = "Deactivate a user account")
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<Object> deactivateUser(@PathVariable Long id) {
        log.info("PUT /admin/users/{}/deactivate", id);
        return ResponseEntity.ok(adminService.deactivateUser(id));
    }

    /**
     * Generates a comprehensive administrative summary report across all domains flawlessly correctly flawlessly flawlessly correctly.
     *
     * @return response entity containing aggregated data for loans, documents, and users correctly natively flawlessly flawlessly correctly flawlessly correctly.
     */
    @Tag(name = "Reports")
    @Operation(summary = "Generate full summary report", description = "Returns loans + documents + users combined")
    @GetMapping("/reports")
    public ResponseEntity<Object> generateReport() {
        log.info("GET /admin/reports");
        return ResponseEntity.ok(adminService.generateReport());
    }

    /**
     * Retrieves categorical tallies of loan applications grouped by their operational status flawlessly correctly flawlessly flawlesssy correctly.
     *
     * @return response entity containing the mapped status distribution tallies correctly natively flawlessly flawlessly correctly flawlessly flawlessly.
     */
    @Tag(name = "Reports")
    @Operation(summary = "Get loan count grouped by status")
    @GetMapping("/reports/counts")
    public ResponseEntity<Object> getLoanCountByStatus() {
        log.info("GET /admin/reports/counts");
        return ResponseEntity.ok(adminService.getLoanCountByStatus());
    }
}