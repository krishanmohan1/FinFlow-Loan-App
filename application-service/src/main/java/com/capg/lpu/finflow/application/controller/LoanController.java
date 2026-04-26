package com.capg.lpu.finflow.application.controller;

import com.capg.lpu.finflow.application.dto.LoanApplicationRequest;
import com.capg.lpu.finflow.application.dto.LoanStatusUpdateRequest;
import com.capg.lpu.finflow.application.entity.LoanApplication;
import com.capg.lpu.finflow.application.service.LoanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for managing loan applications.
 * Provides endpoints for creating, retrieving, updating, and deleting loan application records.
 * Enforces role-based access control (RBAC) via security headers.
 */
@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@Validated
@Tag(name = "Loan Application", description = "Apply for loans and manage applications")
@Slf4j
public class LoanController {

    private final LoanService loanService;

    /**
     * Submits a new loan application.
     * The application is associated with the authenticated user provided in the header.
     *
     * @param loan The loan application data submitted in the request body.
     * @param authUsername The username of the authenticated user submitting the application.
     * @return The created loan application entity.
     */
    
    @Operation(summary = "Apply for a loan", description = "USER submits a new loan application")
    @PostMapping("/apply")
    public ResponseEntity<LoanApplication> apply(
            @Valid @RequestBody LoanApplicationRequest request,
            @RequestHeader("X-Auth-Username") @NotBlank String authUsername) {

        log.info("POST /application/apply - user: {}", authUsername);
        LoanApplication loan = LoanApplication.builder()
                .username(authUsername)
                .amount(request.getAmount())
                .loanType(request.getLoanType())
                .tenureMonths(request.getTenureMonths())
                .purpose(request.getPurpose())
                .build();
        return ResponseEntity.ok(loanService.apply(loan));
    }

    /**
     * Retrieves all loan applications.
     * ADMIN users may see all system records, while standard users see only their own.
     *
     * @param authUsername The username for filtering user-specific queries.
     * @param authRole The security role used to determine authorization levels.
     * @return A list of loan applications authorized for the requester.
     */
    
    @Operation(summary = "Get all loans", description = "ADMIN sees all loans, USER sees only their own")
    @GetMapping("/all")
    public ResponseEntity<List<LoanApplication>> getAll(
            @RequestHeader("X-Auth-Username") @NotBlank String authUsername,
            @RequestHeader("X-Auth-Role") @NotBlank String authRole) {

        log.info("GET /application/all - user: {}, role: {}", authUsername, authRole);
        if ("ADMIN".equals(authRole)) {
            return ResponseEntity.ok(loanService.getAll());
        }
        return ResponseEntity.ok(loanService.getByUsername(authUsername));
    }

    /**
     * Retrieves a specific loan application by its unique identifier.
     * Enforces security to ensure users can only access their own records unless authorized as ADMIN.
     *
     * @param id The numeric ID of the requested loan application.
     * @param authUsername The authenticated requester's username.
     * @param authRole The requester's security role.
     * @return The requested loan application record.
     */
    @Operation(summary = "Get loan by ID", description = "USER can only see their own, ADMIN sees any")
    @GetMapping("/{id}")
    public ResponseEntity<LoanApplication> getById(
            @PathVariable @Positive Long id,
            @RequestHeader("X-Auth-Username") @NotBlank String authUsername,
            @RequestHeader("X-Auth-Role") @NotBlank String authRole) {

        log.info("GET /application/{} - user: {}, role: {}", id, authUsername, authRole);
        return ResponseEntity.ok(loanService.getByIdSecure(id, authUsername, authRole));
    }

    /**
     * Retrieves all loan applications associated with a specific user.
     * Restricted to ADMIN access.
     *
     * @param username The target username to query.
     * @param authRole The role of the requester.
     * @return A list of loan applications for the specified user.
     * @throws SecurityException if the requester does not have ADMIN privileges.
     */
    @Operation(summary = "Get loans by username", description = "ADMIN only")
    @GetMapping("/user/{username}")
    public ResponseEntity<List<LoanApplication>> getByUsername(
            @PathVariable @NotBlank String username,
            @RequestHeader("X-Auth-Role") @NotBlank String authRole) {

        log.info("GET /application/user/{} - role: {}", username, authRole);
        if (!"ADMIN".equals(authRole)) {
            throw new SecurityException("Only ADMIN can access this endpoint");
        }
        return ResponseEntity.ok(loanService.getByUsername(username));
    }

    /**
     * Filters loan applications based on their operational status.
     * ADMIN users receive all matches across the system, while standard users see matches within their own records.
     *
     * @param status The target status for filtering (e.g., PENDING, APPROVED).
     * @param authUsername The authenticated user's name for filtering.
     * @param authRole The requester's security role.
     * @return A filtered list of loan applications.
     */
    @Operation(summary = "Get loans by status", description = "ADMIN sees all by status, USER sees their own by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanApplication>> getByStatus(
            @PathVariable @NotBlank String status,
            @RequestHeader("X-Auth-Username") @NotBlank String authUsername,
            @RequestHeader("X-Auth-Role") @NotBlank String authRole) {

        log.info("GET /application/status/{} - user: {}, role: {}", status, authUsername, authRole);
        if ("ADMIN".equals(authRole)) {
            return ResponseEntity.ok(loanService.getByStatus(status));
        }
        return ResponseEntity.ok(loanService.getByUsernameAndStatus(authUsername, status));
    }

    /**
     * Updates the status and associated remarks for a specific loan application.
     * Endpoint restricted for administrative decision-making (ADMIN only).
     *
     * @param id The ID of the loan application to update.
     * @param request The update request containing the new status and administrative notes.
     * @param authRole The security role of the requester.
     * @return The updated loan application entity.
     * @throws SecurityException if the requester does not have ADMIN privileges.
     */
    @Operation(summary = "Update loan status", description = "ADMIN only - approve or reject a loan")
    @PutMapping("/status/{id}")
    public ResponseEntity<LoanApplication> updateStatus(
            @PathVariable @Positive Long id,
            @Valid @RequestBody LoanStatusUpdateRequest request,
            @RequestHeader("X-Auth-Role") @NotBlank String authRole) {

        log.info("PUT /application/status/{} - {}", id, request.getStatus());
        if (!"ADMIN".equals(authRole)) {
            throw new SecurityException("Only ADMIN can update loan status");
        }
        return ResponseEntity.ok(loanService.updateStatus(id, request));
    }

    /**
     * Permanently deletes a loan application record from the system.
     * Operation is restricted to ADMIN users.
     *
     * @param id The ID of the application to delete.
     * @param authRole The security role of the requester.
     * @return A confirmation message indicating successful deletion.
     * @throws SecurityException if the requester does not have ADMIN privileges.
     */
    @Operation(summary = "Delete a loan application", description = "ADMIN only")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable @Positive Long id,
            @RequestHeader("X-Auth-Role") @NotBlank String authRole) {

        log.info("DELETE /application/{}", id);
        if (!"ADMIN".equals(authRole)) {
            throw new SecurityException("Only ADMIN can delete loans");
        }
        return ResponseEntity.ok(loanService.delete(id));
    }

    /**
     * Allows a borrower to withdraw their own pending or under-review loan application.
     *
     * @param id The loan application identifier.
     * @param authUsername The borrower username.
     * @param authRole The requester role.
     * @return The updated withdrawn loan record.
     */
    @Operation(summary = "Withdraw my loan application", description = "USER withdraws their own PENDING or UNDER_REVIEW loan")
    @PutMapping("/{id}/withdraw")
    public ResponseEntity<LoanApplication> withdraw(
            @PathVariable @Positive Long id,
            @RequestHeader("X-Auth-Username") @NotBlank String authUsername,
            @RequestHeader("X-Auth-Role") @NotBlank String authRole) {

        log.info("PUT /application/{}/withdraw - user: {}, role: {}", id, authUsername, authRole);
        if ("ADMIN".equals(authRole)) {
            throw new SecurityException("Admin cannot withdraw applications on behalf of borrowers from this endpoint");
        }
        return ResponseEntity.ok(loanService.withdraw(id, authUsername));
    }
}
