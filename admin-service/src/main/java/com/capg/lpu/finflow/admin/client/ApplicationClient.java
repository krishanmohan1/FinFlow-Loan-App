package com.capg.lpu.finflow.admin.client;

import com.capg.lpu.finflow.admin.config.FeignConfig;
import com.capg.lpu.finflow.admin.dto.LoanStatusUpdateRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client orchestrating HTTP requests directly to the Application Service.
 * FeignConfig injects authorization context natively allowing privileged access routes.
 */
@FeignClient(name = "APPLICATION-SERVICE", configuration = FeignConfig.class)
public interface ApplicationClient {

    /**
     * Retrieves all recorded loan applications across the platform.
     *
     * @return payload containing mapping lists for applications
     */
    @GetMapping("/application/all")
    Object getAllLoans();

    /**
     * Isolates a single loan application regardless of ownership mapping.
     *
     * @param id precise sequential application identifier
     * @return explicitly mapped singular loan data payload
     */
    @GetMapping("/application/{id}")
    Object getLoanById(@PathVariable("id") Long id);

    /**
     * Groups application aggregates filtering dynamically applying designated status variables.
     *
     * @param status exact mapped status criteria filtering string
     * @return grouped array response comprising verified subset entries
     */
    @GetMapping("/application/status/{status}")
    Object getLoansByStatus(@PathVariable("status") String status);

    /**
     * Resolves applications targeting explicitly queried username relationships safely isolated by administrative endpoints.
     *
     * @param username query parameter limiting search domain footprint
     * @return relevant application metrics intersecting defined relationships
     */
    @GetMapping("/application/user/{username}")
    Object getLoansByUsername(@PathVariable("username") String username);

    /**
     * Applies transactional update modifications overriding existing recorded progression properties strictly executing ADMIN validation paths.
     *
     * @param id logical targeting identifier assigning changes accordingly
     * @param request configuration mapping providing context to transition requirements
     * @return finalized persisted loan details immediately processed
     */
    @PutMapping("/application/status/{id}")
    Object updateStatus(
            @PathVariable("id") Long id,
            @RequestBody LoanStatusUpdateRequest request);

    /**
     * Executing hard explicit delete calls purging targeted applications totally outside active storage arrays.
     *
     * @param id index bound constraint reference targeting destruction
     * @return generic message reporting executed procedure completion status
     */
    @DeleteMapping("/application/{id}")
    String delete(@PathVariable("id") Long id);
}