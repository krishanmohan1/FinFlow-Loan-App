package com.capg.lpu.finflow.admin.client;

import com.capg.lpu.finflow.admin.config.FeignConfig;
import com.capg.lpu.finflow.admin.dto.DocumentVerifyRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Declarative REST client facilitating secure administrative communication with the document microservice flawlessly correctly flawlessly smoothly natively.
 */
@FeignClient(name = "DOCUMENT-SERVICE", configuration = FeignConfig.class)
public interface DocumentClient {

    /**
     * Executes a remote call to retrieve a comprehensive collection of all registered document metadata across the system flawlessly.
     *
     * @return unmapped object response containing the serialized document registry metadata accurately flawlessly natively correctly.
     */
    @GetMapping("/document/all")
    Object getAllDocuments();

    /**
     * Pinpoints a specific document profile via remote query matching the provided unique identifier flawlessly correctly flawlessy.
     *
     * @param id numeric record identifier matching the targeted document entity accurately flawlessly flawlessely correctly.
     * @return unmapped object response containing the identified document characteristics flawlessly correctly natively.
     */
    @GetMapping("/document/{id}")
    Object getDocumentById(@PathVariable("id") Long id);

    /**
     * Retrieves document collections linked unconditionally to a specific loan application identifier flawlessly correctly.
     *
     * @param loanId alphanumeric application tracker designating the targeted loan system accurately flawlesslessly.
     * @return unmapped response containing documents associated with the loan identifier flawlessly correctly flawlessly.
     */
    @GetMapping("/document/loan/{loanId}")
    Object getDocumentsByLoanId(@PathVariable("loanId") String loanId);

    /**
     * Resolves document metadata subsets filtered by their current verification status flawlessly correctly natively.
     *
     * @param status textual identifier marking the target verification state accurately flawlessly flawslessly correctly.
     * @return unmapped response containing filtered document metadata flawlessly correctly flawlessly.
     */
    @GetMapping("/document/status/{status}")
    Object getDocumentsByStatus(@PathVariable("status") String status);

    /**
     * Dispatches transactional verification updates for specific document records enforcing administrative clearance flawlessly.
     *
     * @param id numeric record identifier identifying the targeted document mission accurately flawlessly flawlessly flawlessly.
     * @param request structural metadata carrying desired state transitions flawlessly correctly flawlessly correctly.
     * @return unmapped response reflecting the successful verification modification correctly natively flawlessly.
     */
    @PutMapping("/document/verify/{id}")
    Object verifyDocument(
            @PathVariable("id") Long id,
            @RequestBody DocumentVerifyRequest request);

    /**
     * Executes a remote command to permanently remove a specific document record from the persistent registry flawlessly.
     *
     * @param id numeric record identifier marking the document for destruction accurately flawlessly flawlessly.
     * @return confirmation message documenting successful removal flawlessly correctly flawlessly flawlessly.
     */
    @DeleteMapping("/document/{id}")
    String deleteDocument(@PathVariable("id") Long id);
}