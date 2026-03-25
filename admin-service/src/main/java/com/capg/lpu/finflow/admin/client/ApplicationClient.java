package com.capg.lpu.finflow.admin.client;

import com.capg.lpu.finflow.admin.config.FeignConfig;
import com.capg.lpu.finflow.admin.dto.LoanStatusUpdateRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

// ✅ FeignConfig injects X-Auth-Role: ADMIN and X-Auth-Username: admin
//    into every request automatically — no need to repeat headers per method
@FeignClient(name = "APPLICATION-SERVICE", configuration = FeignConfig.class)
public interface ApplicationClient {

    // ✅ Get all loan applications — ADMIN sees all
    @GetMapping("/application/all")
    Object getAllLoans();

    // ✅ Get single loan by ID — ADMIN can access any loan
    @GetMapping("/application/{id}")
    Object getLoanById(@PathVariable("id") Long id);

    // ✅ Get loans filtered by status — ADMIN gets all loans of that status
    @GetMapping("/application/status/{status}")
    Object getLoansByStatus(@PathVariable("status") String status);

    // ✅ Get loans by a specific username — ADMIN only endpoint
    @GetMapping("/application/user/{username}")
    Object getLoansByUsername(@PathVariable("username") String username);

    // ✅ Update loan status (approve/reject/review) — ADMIN only
    @PutMapping("/application/status/{id}")
    Object updateStatus(
            @PathVariable("id") Long id,
            @RequestBody LoanStatusUpdateRequest request);

    // ✅ Delete a loan application — ADMIN only
    @DeleteMapping("/application/{id}")
    String delete(@PathVariable("id") Long id);
}