package com.finflow.application.controller;

import com.finflow.application.dto.LoanStatusUpdateRequest;
import com.finflow.application.entity.LoanApplication;
import com.finflow.application.service.LoanService;
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
    public ResponseEntity<LoanApplication> apply(@RequestBody LoanApplication loan) {
        log.info("POST /application/apply → user: {}", loan.getUsername());
        return ResponseEntity.ok(loanService.apply(loan));
    }

    // ✅ USER + ADMIN → Get all
    @GetMapping("/all")
    public ResponseEntity<List<LoanApplication>> getAll() {
        log.info("GET /application/all");
        return ResponseEntity.ok(loanService.getAll());
    }

    // ✅ USER + ADMIN → Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<LoanApplication> getById(@PathVariable Long id) {
        log.info("GET /application/{}", id);
        return ResponseEntity.ok(loanService.getById(id));
    }

    // ✅ USER → Get my loans
    @GetMapping("/user/{username}")
    public ResponseEntity<List<LoanApplication>> getByUsername(@PathVariable String username) {
        log.info("GET /application/user/{}", username);
        return ResponseEntity.ok(loanService.getByUsername(username));
    }

    // ✅ ADMIN → Get by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanApplication>> getByStatus(@PathVariable String status) {
        log.info("GET /application/status/{}", status);
        return ResponseEntity.ok(loanService.getByStatus(status));
    }

    // ✅ ADMIN → Update status via Feign from Admin Service
    @PutMapping("/status/{id}")
    public ResponseEntity<LoanApplication> updateStatus(
            @PathVariable Long id,
            @RequestBody LoanStatusUpdateRequest request) {
        log.info("PUT /application/status/{} → {}", id, request.getStatus());
        return ResponseEntity.ok(loanService.updateStatus(id, request));
    }

    // ✅ ADMIN → Delete via Feign from Admin Service
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        log.info("DELETE /application/{}", id);
        return ResponseEntity.ok(loanService.delete(id));
    }
}