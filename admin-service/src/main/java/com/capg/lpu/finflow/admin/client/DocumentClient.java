package com.capg.lpu.finflow.admin.client;

import com.capg.lpu.finflow.admin.config.FeignConfig;
import com.capg.lpu.finflow.admin.dto.DocumentVerifyRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client for communicating with the Document Microservice.
 * Provides administrative access to document storage and verification endpoints.
 */
@FeignClient(name = "DOCUMENT-SERVICE", configuration = FeignConfig.class)
public interface DocumentClient {

    /**
     * Retrieves all document metadata from the document service.
     *
     * @return A list-like object containing all document data.
     */
    @GetMapping("/document/all")
    Object getAllDocuments();

    /**
     * Retrieves a specific document's metadata by its ID.
     *
     * @param id The unique identifier of the document.
     * @return The requested document metadata.
     */
    @GetMapping("/document/{id}")
    Object getDocumentById(@PathVariable("id") Long id);

    /**
     * Retrieves all documents associated with a specific loan ID.
     *
     * @param loanId The identifier of the loan application.
     * @return A list-like object containing documents linked to the loan.
     */
    @GetMapping("/document/loan/{loanId}")
    Object getDocumentsByLoanId(@PathVariable("loanId") String loanId);

    /**
     * Retrieves documents filtered by their current verification status.
     *
     * @param status The status to filter by (e.g., PENDING, VERIFIED, REJECTED).
     * @return A list-like object containing matching document metadata.
     */
    @GetMapping("/document/status/{status}")
    Object getDocumentsByStatus(@PathVariable("status") String status);

    /**
     * Updates the verification status of a specific document.
     *
     * @param id The ID of the document to verify.
     * @param request The object containing the new status and administrative remarks.
     * @return The updated document metadata.
     */
    @PutMapping("/document/verify/{id}")
    Object verifyDocument(
            @PathVariable("id") Long id,
            @RequestBody DocumentVerifyRequest request);

    /**
     * Deletes a specific document record from the document service.
     *
     * @param id The ID of the document to delete.
     * @return A confirmation string upon successful deletion.
     */
    @DeleteMapping("/document/{id}")
    String deleteDocument(@PathVariable("id") Long id);
}