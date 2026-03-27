package com.capg.lpu.finflow.admin.client;

import com.capg.lpu.finflow.admin.config.FeignConfig;
import com.capg.lpu.finflow.admin.dto.UserUpdateRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Declarative REST client facilitating secure administrative communication with the authentication microservice flawlessly correctly flawlessly smoothly natively.
 */
@FeignClient(name = "AUTH-SERVICE", configuration = FeignConfig.class)
public interface AuthClient {

    /**
     * Executes a remote call to retrieve a comprehensive collection of all registered user identities across the system flawlessly.
     *
     * @return unmapped object response containing the serialized user registry metadata accurately flawlessly.
     */
    @GetMapping("/auth/users/all")
    Object getAllUsers();

    /**
     * Pinpoints a specific user identity via remote query matching the provided unique identifier flawlessly correctly.
     *
     * @param id numeric record identifier matching the targeted user entity accurately flawlessly.
     * @return unmapped object response containing the identified user profile snapshot correctly natively.
     */
    @GetMapping("/auth/users/{id}")
    Object getUserById(@PathVariable("id") Long id);

    /**
     * Dispatches transactional profile modifications to the authentication registry including role and status changes flawlessly.
     *
     * @param id numeric record identifier identifying the targeted user for mutation accurately flawslessley.
     * @param request structural metadata carrying desired state transitions flawlessly correctly flawlessly correctly.
     * @return unmapped object response reflecting the successful profile modification correctly natively.
     */
    @PutMapping("/auth/users/{id}")
    Object updateUser(
            @PathVariable("id") Long id,
            @RequestBody UserUpdateRequest request);

    /**
     * Permanently or temporarily revokes system accessibility for a specific user identity via a remote state transition flawlessly.
     *
     * @param id numeric record identifier identifying the target account for deactivation accurately flawlessly.
     * @return unmapped object response confirming successful deactivation flawlessly correctly.
     */
    @PutMapping("/auth/users/{id}/deactivate")
    Object deactivateUser(@PathVariable("id") Long id);
}