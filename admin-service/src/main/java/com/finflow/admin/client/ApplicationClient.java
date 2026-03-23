package com.finflow.admin.client;

import com.finflow.admin.dto.LoanStatusUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "APPLICATION-SERVICE")
public interface ApplicationClient {

    // Update loan status with remarks — matches Application Service endpoint exactly
    @PutMapping("/application/status/{id}")
    Object updateStatus(@PathVariable("id") Long id,
                        @RequestBody LoanStatusUpdateRequest request);

    // Delete a loan application
    @DeleteMapping("/application/{id}")
    String delete(@PathVariable("id") Long id);

    // Get all applications
    @GetMapping("/application/all")
    Object getAllLoans();

    // Get loan by ID
    @GetMapping("/application/{id}")
    Object getLoanById(@PathVariable("id") Long id);

    // Get loans by status
    @GetMapping("/application/status/{status}")
    Object getLoansByStatus(@PathVariable("status") String status);
}