package com.capg.lpu.finflow.admin.client;

import com.capg.lpu.finflow.admin.config.FeignConfig;
import com.capg.lpu.finflow.admin.dto.UserUpdateRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Interface mapping declarative REST operations binding the Admin Service with the Auth Service capabilities.
 */
@FeignClient(name = "AUTH-SERVICE", configuration = FeignConfig.class)
public interface AuthClient {

    /**
     * Proxies HTTP requests returning global arrays tracking total active/inactive registry assignments.
     *
     * @return object model deserialized holding exhaustive list outputs
     */
    @GetMapping("/auth/users/all")
    Object getAllUsers();

    /**
     * Scans authentication registry returning structured details restricted matching precise target identifiers.
     *
     * @param id persistent database id key
     * @return deserialized mapping explicitly revealing parameters associated exclusively
     */
    @GetMapping("/auth/users/{id}")
    Object getUserById(@PathVariable("id") Long id);

    /**
     * Pushes state transitioning properties modifying fundamental baseline characteristics managing user boundaries securely.
     *
     * @param id precise sequential key
     * @param request dynamically structured payload conveying strict update fields required
     * @return resulting output snapshot tracking processed database configurations logically
     */
    @PutMapping("/auth/users/{id}")
    Object updateUser(
            @PathVariable("id") Long id,
            @RequestBody UserUpdateRequest request);

    /**
     * Intercepts and flips explicit boolean toggles prohibiting future authentication sessions securely locking designated accounts.
     *
     * @param id precise sequential key isolating target footprint
     * @return system resulting dataset documenting applied restriction
     */
    @PutMapping("/auth/users/{id}/deactivate")
    Object deactivateUser(@PathVariable("id") Long id);
}