package com.capg.lpu.finflow.document.controller;

import com.capg.lpu.finflow.document.entity.Document;
import com.capg.lpu.finflow.document.service.DocumentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Document", description = "Upload, verify and manage loan documents")
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;

    @Operation(summary = "Upload a document", description = "USER uploads a document for their loan")
    @PostMapping("/upload")
    public ResponseEntity<Document> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("loanId") String loanId,
            @RequestParam("documentType") String documentType,
            @RequestHeader("X-Auth-Username") String username) {

        log.info("POST /document/upload → user: {}, loanId: {}, type: {}", username, loanId, documentType);
        return ResponseEntity.ok(documentService.upload(file, loanId, documentType, username));
    }

    @Operation(summary = "Update a document file", description = "USER updates their own, ADMIN can update any")
    @PutMapping("/update/{id}")
    public ResponseEntity<Document> update(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-Auth-Username") String username,
            @RequestHeader("X-Auth-Role") String role) {

        log.info("PUT /document/update/{} → user: {}, role: {}", id, username, role);
        if (!"ADMIN".equals(role)) {
            Document existing = documentService.getById(id);
            if (!existing.getUsername().equals(username)) {
                throw new SecurityException("Access denied. This document does not belong to you.");
            }
        }
        return ResponseEntity.ok(documentService.updateFile(id, file));
    }

    @Operation(summary = "Get all documents", description = "ADMIN only")
    @GetMapping("/all")
    public ResponseEntity<List<Document>> getAll(
            @RequestHeader("X-Auth-Role") String role) {

        log.info("GET /document/all → role: {}", role);
        if (!"ADMIN".equals(role)) {
            throw new SecurityException("Only ADMIN can view all documents");
        }
        return ResponseEntity.ok(documentService.getAll());
    }

    @Operation(summary = "Get document by ID", description = "USER sees their own, ADMIN sees any")
    @GetMapping("/{id}")
    public ResponseEntity<Document> getById(
            @PathVariable Long id,
            @RequestHeader("X-Auth-Username") String username,
            @RequestHeader("X-Auth-Role") String role) {

        log.info("GET /document/{} → user: {}, role: {}", id, username, role);
        Document doc = documentService.getById(id);
        if (!"ADMIN".equals(role) && !doc.getUsername().equals(username)) {
            throw new SecurityException("Access denied. This document does not belong to you.");
        }
        return ResponseEntity.ok(doc);
    }

    @Operation(summary = "Get documents by loan ID", description = "USER sees their own for that loan, ADMIN sees all")
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<Document>> getByLoan(
            @PathVariable String loanId,
            @RequestHeader("X-Auth-Username") String username,
            @RequestHeader("X-Auth-Role") String role) {

        log.info("GET /document/loan/{} → user: {}, role: {}", loanId, username, role);
        if ("ADMIN".equals(role)) {
            return ResponseEntity.ok(documentService.getByLoanId(loanId));
        }
        return ResponseEntity.ok(documentService.getByUsernameAndLoanId(username, loanId));
    }

    @Operation(summary = "Get my documents", description = "USER gets all their own uploaded documents")
    @GetMapping("/my")
    public ResponseEntity<List<Document>> getMyDocuments(
            @RequestHeader("X-Auth-Username") String username) {

        log.info("GET /document/my → user: {}", username);
        return ResponseEntity.ok(documentService.getByUsername(username));
    }

    @Operation(summary = "Get documents by verification status", description = "ADMIN only — PENDING, VERIFIED, REJECTED")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Document>> getByStatus(
            @PathVariable String status,
            @RequestHeader("X-Auth-Role") String role) {

        log.info("GET /document/status/{} → role: {}", status, role);
        if (!"ADMIN".equals(role)) {
            throw new SecurityException("Only ADMIN can filter documents by verification status");
        }
        return ResponseEntity.ok(documentService.getByVerificationStatus(status));
    }

    @Operation(summary = "Verify or reject a document", description = "ADMIN only — set status to VERIFIED or REJECTED")
    @PutMapping("/verify/{id}")
    public ResponseEntity<Document> verify(
            @PathVariable Long id,
            @RequestBody VerifyRequest request,
            @RequestHeader("X-Auth-Role") String role) {

        log.info("PUT /document/verify/{} → status: {}, role: {}", id, request.getStatus(), role);
        if (!"ADMIN".equals(role)) {
            throw new SecurityException("Only ADMIN can verify documents");
        }
        return ResponseEntity.ok(documentService.verifyDocument(id, request.getStatus(), request.getRemarks()));
    }

    @Operation(summary = "Delete a document", description = "ADMIN only")
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

    @Data
    static class VerifyRequest {
        private String status;
        private String remarks;
    }
}