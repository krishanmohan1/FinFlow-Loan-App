package com.capg.lpu.finflow.admin.client;

import com.capg.lpu.finflow.admin.config.FeignConfig;
import com.capg.lpu.finflow.admin.dto.LoanStatusUpdateRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client for communicating with the Application Microservice.
 * Provides administrative access to loan application management endpoints.
 */
@FeignClient(name = "APPLICATION-SERVICE", configuration = FeignConfig.class)
public interface ApplicationClient {

    /**
     * Retrieves all loan applications from the application service.
     *
     * @return A list-like object containing all loan application data.
     */
    @GetMapping("/application/all")
    Object getAllLoans();

    /**
     * Retrieves a specific loan application by its ID.
     *
     * @param id The unique identifier of the loan application.
     * @return The requested loan application data.
     */
    @GetMapping("/application/{id}")
    Object getLoanById(@PathVariable("id") Long id);

    /**
     * Retrieves loan applications filtered by their current status.
     *
     * @param status The status to filter by (e.g., PENDING, APPROVED, REJECTED).
     * @return A list-like object containing matching loan applications.
     */
    @GetMapping("/application/status/{status}")
    Object getLoansByStatus(@PathVariable("status") String status);

    /**
     * Retrieves all loan applications associated with a specific username.
     *
     * @param username The username of the applicant.
     * @return A list-like object containing the user's loan applications.
     */
    @GetMapping("/application/user/{username}")
    Object getLoansByUsername(@PathVariable("username") String username);

    /**
     * Updates the status of a specific loan application.
     *
     * @param id The ID of the loan application to update.
     * @param request The object containing the new status and administrative remarks.
     * @return The updated loan application data.
     */
    @PutMapping("/application/status/{id}")
    Object updateStatus(
            @PathVariable("id") Long id,
            @RequestBody LoanStatusUpdateRequest request);

    /**
     * Deletes a specific loan application from the application service.
     *
     * @param id The ID of the loan application to delete.
     * @return A confirmation string upon successful deletion.
     */
    @DeleteMapping("/application/{id}")
    String delete(@PathVariable("id") Long id);
}