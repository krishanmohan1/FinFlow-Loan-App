package com.capg.lpu.finflow.admin.client;

import com.capg.lpu.finflow.admin.config.FeignConfig;
import com.capg.lpu.finflow.admin.dto.DocumentVerifyRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

// ✅ FeignConfig injects X-Auth-Role: ADMIN automatically
@FeignClient(name = "DOCUMENT-SERVICE", configuration = FeignConfig.class)
public interface DocumentClient {

    // ✅ Get all documents — ADMIN only endpoint in Document Service
    @GetMapping("/document/all")
    Object getAllDocuments();

    // ✅ Get single document by ID — ADMIN can access any document
    @GetMapping("/document/{id}")
    Object getDocumentById(@PathVariable("id") Long id);

    // ✅ Get all documents linked to a specific loan
    @GetMapping("/document/loan/{loanId}")
    Object getDocumentsByLoanId(@PathVariable("loanId") String loanId);

    // ✅ FIX: Correct endpoint — Document Service uses /document/my for user docs
    //         but ADMIN fetches by username via /document/all + filter is not available
    //         The correct admin path is: get all, then filter — OR use loanId
    //         For now: ADMIN calls /document/all and the service returns all docs
    //         This method calls the status filter endpoint which is ADMIN-only
    @GetMapping("/document/status/{status}")
    Object getDocumentsByStatus(@PathVariable("status") String status);

    // ✅ Verify or reject a document — ADMIN only
    @PutMapping("/document/verify/{id}")
    Object verifyDocument(
            @PathVariable("id") Long id,
            @RequestBody DocumentVerifyRequest request);

    // ✅ Delete a document — ADMIN only
    @DeleteMapping("/document/{id}")
    String deleteDocument(@PathVariable("id") Long id);
}