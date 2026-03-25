package com.capg.lpu.finflow.admin.client;

import com.capg.lpu.finflow.admin.config.FeignConfig;
import com.capg.lpu.finflow.admin.dto.UserUpdateRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

// ✅ FeignConfig injects X-Auth-Role: ADMIN automatically
@FeignClient(name = "AUTH-SERVICE", configuration = FeignConfig.class)
public interface AuthClient {

    // ✅ Get all registered users
    @GetMapping("/auth/users/all")
    Object getAllUsers();

    // ✅ Get user by ID
    @GetMapping("/auth/users/{id}")
    Object getUserById(@PathVariable("id") Long id);

    // ✅ Update user role or active status
    @PutMapping("/auth/users/{id}")
    Object updateUser(
            @PathVariable("id") Long id,
            @RequestBody UserUpdateRequest request);

    // ✅ Deactivate a user account
    @PutMapping("/auth/users/{id}/deactivate")
    Object deactivateUser(@PathVariable("id") Long id);
}