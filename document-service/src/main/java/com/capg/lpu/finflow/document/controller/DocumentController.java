package com.capg.lpu.finflow.document.controller;

import com.capg.lpu.finflow.document.entity.Document;
import com.capg.lpu.finflow.document.service.DocumentService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;

    // ✅ USER → Upload a document for their loan
    @PostMapping("/upload")
    public ResponseEntity<Document> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("loanId") String loanId,
            @RequestParam("documentType") String documentType,
            @RequestHeader("X-Auth-Username") String username) {

        log.info("POST /document/upload → user: {}, loanId: {}, type: {}",
                username, loanId, documentType);

        return ResponseEntity.ok(
                documentService.upload(file, loanId, documentType, username)
        );
    }

    // ✅ USER → Update their own document file
    // ✅ ADMIN → Can update any document
    @PutMapping("/update/{id}")
    public ResponseEntity<Document> update(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-Auth-Username") String username,
            @RequestHeader("X-Auth-Role") String role) {

        log.info("PUT /document/update/{} → user: {}, role: {}", id, username, role);

        // ✅ FIX: If USER, verify the document belongs to them
        if (!"ADMIN".equals(role)) {
            Document existing = documentService.getById(id);
            if (!existing.getUsername().equals(username)) {
                throw new SecurityException(
                        "Access denied. This document does not belong to you."
                );
            }
        }

        return ResponseEntity.ok(documentService.updateFile(id, file));
    }

    // ✅ ADMIN ONLY → Get all documents
    @GetMapping("/all")
    public ResponseEntity<List<Document>> getAll(
            @RequestHeader("X-Auth-Role") String role) {

        log.info("GET /document/all → role: {}", role);

        // ✅ FIX: Was missing role check — anyone could call this before
        if (!"ADMIN".equals(role)) {
            throw new SecurityException("Only ADMIN can view all documents");
        }

        return ResponseEntity.ok(documentService.getAll());
    }

    // ✅ USER → Only own document
    // ✅ ADMIN → Any document
    @GetMapping("/{id}")
    public ResponseEntity<Document> getById(
            @PathVariable Long id,
            @RequestHeader("X-Auth-Username") String username,
            @RequestHeader("X-Auth-Role") String role) {

        log.info("GET /document/{} → user: {}, role: {}", id, username, role);

        Document doc = documentService.getById(id);

        // ✅ FIX: Block user from viewing another user's document
        if (!"ADMIN".equals(role) && !doc.getUsername().equals(username)) {
            throw new SecurityException(
                    "Access denied. This document does not belong to you."
            );
        }

        return ResponseEntity.ok(doc);
    }

    // ✅ USER → Only own documents for a loan
    // ✅ ADMIN → All documents for a loan
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<Document>> getByLoan(
            @PathVariable String loanId,
            @RequestHeader("X-Auth-Username") String username,
            @RequestHeader("X-Auth-Role") String role) {

        log.info("GET /document/loan/{} → user: {}, role: {}", loanId, username, role);

        if ("ADMIN".equals(role)) {
            return ResponseEntity.ok(documentService.getByLoanId(loanId));
        }

        // ✅ FIX: USER only gets their own docs for that loan
        return ResponseEntity.ok(
                documentService.getByUsernameAndLoanId(username, loanId)
        );
    }

    // ✅ USER → Get all their own documents
    @GetMapping("/my")
    public ResponseEntity<List<Document>> getMyDocuments(
            @RequestHeader("X-Auth-Username") String username) {

        log.info("GET /document/my → user: {}", username);
        return ResponseEntity.ok(documentService.getByUsername(username));
    }

    // ✅ ADMIN ONLY → Get documents by verification status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Document>> getByStatus(
            @PathVariable String status,
            @RequestHeader("X-Auth-Role") String role) {

        log.info("GET /document/status/{} → role: {}", status, role);

        if (!"ADMIN".equals(role)) {
            throw new SecurityException(
                    "Only ADMIN can filter documents by verification status"
            );
        }

        return ResponseEntity.ok(documentService.getByVerificationStatus(status));
    }

    // ✅ ADMIN ONLY → Verify or Reject a document
    @PutMapping("/verify/{id}")
    public ResponseEntity<Document> verify(
            @PathVariable Long id,
            @RequestBody VerifyRequest request,
            @RequestHeader("X-Auth-Role") String role) {

        log.info("PUT /document/verify/{} → status: {}, role: {}", id, request.getStatus(), role);

        // ✅ FIX: Was missing role check — anyone could verify before
        if (!"ADMIN".equals(role)) {
            throw new SecurityException("Only ADMIN can verify documents");
        }

        return ResponseEntity.ok(
                documentService.verifyDocument(id, request.getStatus(), request.getRemarks())
        );
    }

    // ✅ ADMIN ONLY → Delete a document
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id,
            @RequestHeader("X-Auth-Role") String role) {

        log.info("DELETE /document/{} → role: {}", id, role);

        if (!"ADMIN".equals(role)) {
            throw new SecurityException("Only ADMIN can delete documents");
        }

        return ResponseEntity.ok(documentService.deleteDocument(id));
    }

    // ✅ Inner DTO class for verify request body
    @Data
    static class VerifyRequest {
        private String status;   // VERIFIED or REJECTED
        private String remarks;  // Admin note
    }
}