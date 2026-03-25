package com.capg.lpu.finflow.application.controller;

import com.capg.lpu.finflow.application.dto.LoanStatusUpdateRequest;
import com.capg.lpu.finflow.application.entity.LoanApplication;
import com.capg.lpu.finflow.application.service.LoanService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class LoanController {

    private static final Logger log = LoggerFactory.getLogger(LoanController.class);

    private final LoanService loanService;

    // ✅ USER → Apply for loan
    @PostMapping("/apply")
    public ResponseEntity<LoanApplication> apply(
            @RequestBody LoanApplication loan,
            @RequestHeader("X-Auth-Username") String authUsername) {

        log.info("POST /application/apply → user: {}", authUsername);
        loan.setUsername(authUsername);
        return ResponseEntity.ok(loanService.apply(loan));
    }

    // ✅ USER → Get only MY loans
    // ✅ ADMIN → Get all loans
    @GetMapping("/all")
    public ResponseEntity<List<LoanApplication>> getAll(
            @RequestHeader("X-Auth-Username") String authUsername,
            @RequestHeader("X-Auth-Role") String authRole) {

        log.info("GET /application/all → user: {}, role: {}", authUsername, authRole);

        if ("ADMIN".equals(authRole)) {
            return ResponseEntity.ok(loanService.getAll());
        }
        return ResponseEntity.ok(loanService.getByUsername(authUsername));
    }

    // ✅ USER → Only own loan
    // ✅ ADMIN → Any loan
    @GetMapping("/{id}")
    public ResponseEntity<LoanApplication> getById(
            @PathVariable Long id,
            @RequestHeader("X-Auth-Username") String authUsername,
            @RequestHeader("X-Auth-Role") String authRole) {

        log.info("GET /application/{} → user: {}, role: {}", id, authUsername, authRole);
        return ResponseEntity.ok(loanService.getByIdSecure(id, authUsername, authRole));
    }

    // ✅ ADMIN ONLY → Get loans by username
    @GetMapping("/user/{username}")
    public ResponseEntity<List<LoanApplication>> getByUsername(
            @PathVariable String username,
            @RequestHeader("X-Auth-Role") String authRole) {

        log.info("GET /application/user/{} → role: {}", username, authRole);

        if (!"ADMIN".equals(authRole)) {
            throw new SecurityException("Only ADMIN can access this endpoint");
        }

        return ResponseEntity.ok(loanService.getByUsername(username));
    }

    // ✅ USER → Own loans by status
    // ✅ ADMIN → All loans by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanApplication>> getByStatus(
            @PathVariable String status,
            @RequestHeader("X-Auth-Username") String authUsername,
            @RequestHeader("X-Auth-Role") String authRole) {

        log.info("GET /application/status/{} → user: {}, role: {}", status, authUsername, authRole);

        if ("ADMIN".equals(authRole)) {
            return ResponseEntity.ok(loanService.getByStatus(status));
        }

        return ResponseEntity.ok(
                loanService.getByUsernameAndStatus(authUsername, status)
        );
    }

    // ✅ ADMIN ONLY → Update status
    @PutMapping("/status/{id}")
    public ResponseEntity<LoanApplication> updateStatus(
            @PathVariable Long id,
            @RequestBody LoanStatusUpdateRequest request,
            @RequestHeader("X-Auth-Role") String authRole) {

        log.info("PUT /application/status/{} → {}", id, request.getStatus());

        if (!"ADMIN".equals(authRole)) {
            throw new SecurityException("Only ADMIN can update loan status");
        }

        return ResponseEntity.ok(loanService.updateStatus(id, request));
    }

    // ✅ ADMIN ONLY → Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id,
            @RequestHeader("X-Auth-Role") String authRole) {

        log.info("DELETE /application/{}", id);

        if (!"ADMIN".equals(authRole)) {
            throw new SecurityException("Only ADMIN can delete loans");
        }

        return ResponseEntity.ok(loanService.delete(id));
    }
}