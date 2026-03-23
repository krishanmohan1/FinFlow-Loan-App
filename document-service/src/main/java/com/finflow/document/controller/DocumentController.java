package com.finflow.document.controller;

import com.finflow.document.entity.Document;
import com.finflow.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;

    // ✅ Get all documents
    @GetMapping("/all")
    public ResponseEntity<List<Document>> getAll() {
        log.info("GET /document/all");
        return ResponseEntity.ok(documentService.getAll());
    }

    // ✅ Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<Document> getById(@PathVariable Long id) {
        log.info("GET /document/{}", id);
        return ResponseEntity.ok(documentService.getById(id));
    }

    // ✅ Get by username
    @GetMapping("/user/{username}")
    public ResponseEntity<List<Document>> getByUsername(@PathVariable String username) {
        log.info("GET /document/user/{}", username);
        return ResponseEntity.ok(documentService.getByUsername(username));
    }

    // ✅ Get by loan ID
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<Document>> getByLoanId(@PathVariable String loanId) {
        log.info("GET /document/loan/{}", loanId);
        return ResponseEntity.ok(documentService.getByLoanId(loanId));
    }

    // ✅ Get by event type
    @GetMapping("/event/{eventType}")
    public ResponseEntity<List<Document>> getByEventType(@PathVariable String eventType) {
        log.info("GET /document/event/{}", eventType);
        return ResponseEntity.ok(documentService.getByEventType(eventType));
    }
}