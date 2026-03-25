package com.capg.lpu.finflow.document.service;

import com.capg.lpu.finflow.document.entity.Document;
import com.capg.lpu.finflow.document.exception.ResourceNotFoundException;
import com.capg.lpu.finflow.document.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;

    // ✅ FIX: Absolute path — works regardless of where JVM is launched from
    // ✅ Uses user.home so it works on any OS (Windows, Linux, Mac)
    private static final String UPLOAD_DIR =
            System.getProperty("user.home") + File.separator + "finflow-uploads" + File.separator;

    // ✅ Upload a new document
    public Document upload(MultipartFile file,
                           String loanId,
                           String documentType,
                           String username) {

        log.info("📁 Uploading document → user: {}, loanId: {}, type: {}",
                username, loanId, documentType);

        // ✅ FIX: Block duplicate document type for the same loan
        Optional<Document> existing =
                documentRepository.findByLoanIdAndDocumentType(loanId, documentType);

        if (existing.isPresent()) {
            log.warn("⚠️ Duplicate document type [{}] for loanId [{}]", documentType, loanId);
            throw new RuntimeException(
                    "Document of type [" + documentType + "] already uploaded for loan ID: " + loanId
                            + ". Please use the update endpoint instead."
            );
        }

        // ✅ Create upload directory if it doesn't exist
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("📂 Created upload directory: {}", UPLOAD_DIR);
            }
        } catch (Exception e) {
            log.error("🔴 Failed to create upload directory: {}", e.getMessage());
            throw new RuntimeException("Failed to create upload directory");
        }

        // ✅ Generate unique file name to avoid collisions
        String originalFileName = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + originalFileName;
        String filePath = UPLOAD_DIR + fileName;

        // ✅ Save file to disk
        try {
            file.transferTo(new File(filePath));
            log.info("✅ File saved to: {}", filePath);
        } catch (Exception e) {
            log.error("🔴 File upload failed: {}", e.getMessage(), e);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }

        // ✅ Save document record to DB
        // createdAt + verificationStatus auto-set via @PrePersist
        Document doc = Document.builder()
                .username(username)
                .loanId(loanId)
                .documentType(documentType)
                .fileName(fileName)
                .filePath(filePath)
                .verificationStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        Document saved = documentRepository.save(doc);
        log.info("✅ Document saved to DB with ID: {}", saved.getId());
        return saved;
    }

    // ✅ Update an existing document file
    public Document updateFile(Long id, MultipartFile file) {
        log.info("🔄 Updating document ID: {}", id);

        Document doc = getById(id);

        // ✅ FIX: Delete the OLD file from disk before saving new one
        try {
            Path oldFilePath = Paths.get(doc.getFilePath());
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath);
                log.info("🗑️ Old file deleted: {}", doc.getFilePath());
            }
        } catch (Exception e) {
            // Non-critical — log and continue
            log.warn("⚠️ Could not delete old file: {}", e.getMessage());
        }

        // ✅ Save new file to disk
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = UPLOAD_DIR + fileName;

        try {
            file.transferTo(new File(filePath));
            log.info("✅ New file saved to: {}", filePath);
        } catch (Exception e) {
            log.error("🔴 File update failed: {}", e.getMessage(), e);
            throw new RuntimeException("File update failed: " + e.getMessage());
        }

        // ✅ Update document record in DB
        // Reset to PENDING since file changed — needs re-verification
        doc.setFileName(fileName);
        doc.setFilePath(filePath);
        doc.setVerificationStatus("PENDING");
        doc.setVerifiedRemarks(null);

        Document updated = documentRepository.save(doc);
        log.info("✅ Document ID: {} updated successfully", id);
        return updated;
    }

    // ✅ Get all documents (ADMIN only — controller guards this)
    public List<Document> getAll() {
        log.info("📋 Fetching all documents");
        return documentRepository.findAll();
    }

    // ✅ Get single document by ID
    public Document getById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document not found with ID: " + id));
    }

    // ✅ Get all documents by username
    public List<Document> getByUsername(String username) {
        log.info("📋 Fetching documents for user: {}", username);
        return documentRepository.findByUsername(username);
    }

    // ✅ Get all documents linked to a loan
    public List<Document> getByLoanId(String loanId) {
        log.info("📋 Fetching documents for loanId: {}", loanId);
        return documentRepository.findByLoanId(loanId);
    }

    // ✅ Get documents by username and loanId combined
    public List<Document> getByUsernameAndLoanId(String username, String loanId) {
        log.info("📋 Fetching documents for user: {} and loanId: {}", username, loanId);
        return documentRepository.findByUsernameAndLoanId(username, loanId);
    }

    // ✅ Get documents by verification status
    public List<Document> getByVerificationStatus(String status) {
        log.info("📋 Fetching documents with status: {}", status);
        return documentRepository.findByVerificationStatus(status);
    }

    // ✅ ADMIN → Verify or Reject a document
    public Document verifyDocument(Long id, String status, String remarks) {
        log.info("🔍 Verifying document ID: {} → status: {}", id, status);

        // ✅ Validate status value
        if (!status.equals("VERIFIED") && !status.equals("REJECTED")) {
            throw new RuntimeException(
                    "Invalid status: [" + status + "]. Must be VERIFIED or REJECTED."
            );
        }

        Document doc = getById(id);

        doc.setVerificationStatus(status);
        doc.setVerifiedRemarks(remarks);
        // updatedAt auto-set via @PreUpdate in entity

        Document saved = documentRepository.save(doc);
        log.info("✅ Document ID: {} marked as: {}", id, status);
        return saved;
    }

    // ✅ Delete a document (ADMIN only — controller guards this)
    public String deleteDocument(Long id) {
        log.info("🗑️ Deleting document ID: {}", id);

        Document doc = getById(id);

        // ✅ Delete physical file from disk
        try {
            Path filePath = Paths.get(doc.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("🗑️ Physical file deleted: {}", doc.getFilePath());
            }
        } catch (Exception e) {
            log.warn("⚠️ Could not delete physical file: {}", e.getMessage());
        }

        documentRepository.delete(doc);
        log.info("✅ Document ID: {} deleted from DB", id);
        return "Document with ID " + id + " deleted successfully.";
    }
}