package com.finflow.document.service;

import com.finflow.document.entity.Document;
import com.finflow.document.exception.ResourceNotFoundException;
import com.finflow.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;

    public List<Document> getAll() {
        log.info("📋 Fetching all documents");
        return documentRepository.findAll();
    }

    public Document getById(Long id) {
        log.info("🔍 Fetching document by ID: {}", id);
        return documentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("❌ Document not found with ID: {}", id);
                    return new ResourceNotFoundException("Document not found with ID: " + id);
                });
    }

    public List<Document> getByUsername(String username) {
        log.info("👤 Fetching documents for user: {}", username);
        return documentRepository.findByUsername(username);
    }

    public List<Document> getByLoanId(String loanId) {
        log.info("🔗 Fetching documents for loanId: {}", loanId);
        return documentRepository.findByLoanId(loanId);
    }

    public List<Document> getByEventType(String eventType) {
        log.info("📂 Fetching documents for event type: {}", eventType);
        return documentRepository.findByEventType(eventType);
    }
}