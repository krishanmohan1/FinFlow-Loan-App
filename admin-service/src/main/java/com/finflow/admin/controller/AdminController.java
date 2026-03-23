package com.finflow.admin.controller;

import com.finflow.admin.service.AdminService;
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

    // ✅ View all loan applications
    @GetMapping("/loans")
    public ResponseEntity<Object> getAllLoans() {
        log.info("GET /admin/loans");
        return ResponseEntity.ok(adminService.getAllLoans());
    }

    // ✅ View loan by ID
    @GetMapping("/loans/{id}")
    public ResponseEntity<Object> getLoanById(@PathVariable Long id) {
        log.info("GET /admin/loans/{}", id);
        return ResponseEntity.ok(adminService.getLoanById(id));
    }

    // ✅ View loans by status
    @GetMapping("/loans/status/{status}")
    public ResponseEntity<Object> getLoansByStatus(@PathVariable String status) {
        log.info("GET /admin/loans/status/{}", status);
        return ResponseEntity.ok(adminService.getLoansByStatus(status));
    }

    // ✅ Approve loan
    @PutMapping("/approve/{id}")
    public ResponseEntity<Object> approve(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Loan approved by admin") String remarks) {
        log.info("PUT /admin/approve/{} | remarks: {}", id, remarks);
        return ResponseEntity.ok(adminService.approveLoan(id, remarks));
    }

    // ✅ Reject loan
    @PutMapping("/reject/{id}")
    public ResponseEntity<Object> reject(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Loan rejected by admin") String remarks) {
        log.info("PUT /admin/reject/{} | remarks: {}", id, remarks);
        return ResponseEntity.ok(adminService.rejectLoan(id, remarks));
    }

    // ✅ Delete loan
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        log.info("DELETE /admin/delete/{}", id);
        return ResponseEntity.ok(adminService.deleteLoan(id));
    }
}