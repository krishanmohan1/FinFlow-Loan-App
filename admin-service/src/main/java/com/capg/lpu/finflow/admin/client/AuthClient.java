package com.capg.lpu.finflow.admin.client;

import com.capg.lpu.finflow.admin.config.FeignConfig;
import com.capg.lpu.finflow.admin.dto.StaffRegistrationRequest;
import com.capg.lpu.finflow.admin.dto.UserUpdateRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client for communicating with the Authentication Microservice.
 * Provides administrative access to user management and account control endpoints.
 */
@FeignClient(name = "AUTH-SERVICE", configuration = FeignConfig.class)
public interface AuthClient {

    /**
     * Retrieves all registered users from the authentication service.
     *
     * @return A list-like object containing all user identity data.
     */
    @GetMapping("/auth/users/all")
    Object getAllUsers();

    /**
     * Retrieves a specific user profile by its ID.
     *
     * @param id The unique identifier of the user account.
     * @return The requested user profile data.
     */
    @GetMapping("/auth/users/{id}")
    Object getUserById(@PathVariable("id") Long id);

    /**
     * Creates a new internal admin account through the auth service.
     *
     * @param request The staff onboarding request.
     * @return The created staff profile.
     */
    @PostMapping("/auth/admin/register")
    Object registerStaff(@RequestBody StaffRegistrationRequest request);

    /**
     * Updates an existing user's profile information.
     *
     * @param id The ID of the user to update.
     * @param request The object containing updated profile details and role changes.
     * @return The updated user profile data.
     */
    @PutMapping("/auth/users/{id}")
    Object updateUser(
            @PathVariable("id") Long id,
            @RequestBody UserUpdateRequest request);

    /**
     * Deactivates a specific user account.
     *
     * @param id The ID of the user to deactivate.
     * @return A response object confirming the deactivation status.
     */
    @PutMapping("/auth/users/{id}/deactivate")
    Object deactivateUser(@PathVariable("id") Long id);
}
