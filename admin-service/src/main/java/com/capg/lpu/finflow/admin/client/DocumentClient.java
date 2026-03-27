package com.capg.lpu.finflow.admin.client;

import com.capg.lpu.finflow.admin.config.FeignConfig;
import com.capg.lpu.finflow.admin.dto.DocumentVerifyRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Interface coordinating direct access protocols governing downstream interactions connected inherently with Document orchestration endpoints.
 */
@FeignClient(name = "DOCUMENT-SERVICE", configuration = FeignConfig.class)
public interface DocumentClient {

    /**
     * Commands full scale retrieval operations capturing extensive metadata records linked unconditionally to document arrays.
     *
     * @return mapped array payload encapsulating overarching repository data points
     */
    @GetMapping("/document/all")
    Object getAllDocuments();

    /**
     * Executes queries retrieving explicit properties strictly matched validating underlying specific document sequences.
     *
     * @param id identifying target tracking parameter
     * @return securely exposed entity characteristics representing precise document sets
     */
    @GetMapping("/document/{id}")
    Object getDocumentById(@PathVariable("id") Long id);

    /**
     * Constructs array mappings filtering entirely isolating file assets linked specifically supporting dedicated application files.
     *
     * @param loanId alphanumeric application tracker designating associative relationships
     * @return bundled response tracking identified elements bound conditionally
     */
    @GetMapping("/document/loan/{loanId}")
    Object getDocumentsByLoanId(@PathVariable("loanId") String loanId);

    /**
     * Dispatches lookup validations querying endpoints designed handling restrictive filtering mapping explicitly administrative queries.
     *
     * @param status operational variable narrowing result criteria
     * @return resulting verified objects adhering structurally
     */
    @GetMapping("/document/status/{status}")
    Object getDocumentsByStatus(@PathVariable("status") String status);

    /**
     * Executes fundamental state transitioning operations verifying integrity variables updating dependent applications accurately.
     *
     * @param id isolated tracker matching precise target rows exclusively
     * @param request structured constraint modifiers specifying valid progression variables
     * @return confirmed resultant updates persisted successfully over bounded transactions
     */
    @PutMapping("/document/verify/{id}")
    Object verifyDocument(
            @PathVariable("id") Long id,
            @RequestBody DocumentVerifyRequest request);

    /**
     * Submits explicitly commanded deletion sequences unconditionally purging targeted storage tracking sequences securely entirely.
     *
     * @param id targeted indexing variables marking explicitly defined destruction paths
     * @return procedural outcome confirmation string reliably documenting execution
     */
    @DeleteMapping("/document/{id}")
    String deleteDocument(@PathVariable("id") Long id);
}