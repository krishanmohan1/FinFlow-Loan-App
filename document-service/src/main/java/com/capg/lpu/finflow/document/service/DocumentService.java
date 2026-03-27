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

/**
 * Service class for managing document lifecycles.
 * Handles physical file storage, database metadata persistence, and administrative verification workflows.
 */
@Service
@RequiredArgsConstructor
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;

    private static final String UPLOAD_DIR =
            System.getProperty("user.home") + File.separator + "finflow-uploads" + File.separator;

    /**
     * Uploads a document to the server and saves its metadata in the database.
     * Prevents multiple uploads of the same document type for a single loan application.
     *
     * @param file The multi-part file to be stored.
     * @param loanId The identifier of the associated loan application.
     * @param documentType The category of the document.
     * @param username The username of the uploading user.
     * @return The persisted Document entity.
     * @throws RuntimeException If a document of the same type already exists or if file storage fails.
     */
    public Document upload(MultipartFile file,
                           String loanId,
                           String documentType,
                           String username) {

        log.info("Uploading document - user: {}, loanId: {}, type: {}",
                username, loanId, documentType);

        Optional<Document> existing =
                documentRepository.findByLoanIdAndDocumentType(loanId, documentType);

        if (existing.isPresent()) {
            log.warn("Duplicate document type [{}] for loanId [{}]", documentType, loanId);
            throw new RuntimeException(
                    "Document of type [" + documentType + "] already uploaded for loan ID: " + loanId
                            + ". Please use the update endpoint instead."
            );
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", UPLOAD_DIR);
            }
        } catch (Exception e) {
            log.error("Failed to create upload directory: {}", e.getMessage());
            throw new RuntimeException("Failed to create upload directory");
        }

        String originalFileName = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + originalFileName;
        String filePath = UPLOAD_DIR + fileName;

        try {
            file.transferTo(new File(filePath));
            log.info("File saved to: {}", filePath);
        } catch (Exception e) {
            log.error("File upload failed: {}", e.getMessage(), e);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }

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
        log.info("Document saved to DB with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Updates an existing document's file content.
     * Replaces the old file on the filesystem and resets verification status to PENDING.
     *
     * @param id The ID of the document to update.
     * @param file The new file content.
     * @return The updated Document entity.
     * @throws RuntimeException If file replacement or database update fails.
     */
    public Document updateFile(Long id, MultipartFile file) {
        log.info("Updating document ID: {}", id);

        Document doc = getById(id);

        try {
            Path oldFilePath = Paths.get(doc.getFilePath());
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath);
                log.info("Old file deleted: {}", doc.getFilePath());
            }
        } catch (Exception e) {
            log.warn("Could not delete old file: {}", e.getMessage());
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = UPLOAD_DIR + fileName;

        try {
            file.transferTo(new File(filePath));
            log.info("New file saved to: {}", filePath);
        } catch (Exception e) {
            log.error("File update failed: {}", e.getMessage(), e);
            throw new RuntimeException("File update failed: " + e.getMessage());
        }

        doc.setFileName(fileName);
        doc.setFilePath(filePath);
        doc.setVerificationStatus("PENDING");
        doc.setVerifiedRemarks(null);

        Document updated = documentRepository.save(doc);
        log.info("Document ID: {} updated successfully", id);
        return updated;
    }

    /**
     * Retrieves all documents stored in the system.
     *
     * @return A list of all Document entities.
     */
    public List<Document> getAll() {
        log.info("Fetching all documents");
        return documentRepository.findAll();
    }

    /**
     * Retrieves a document by its unique ID.
     *
     * @param id The document's ID.
     * @return The found Document entity.
     * @throws ResourceNotFoundException If no document is found with the given ID.
     */
    public Document getById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document not found with ID: " + id));
    }

    /**
     * Retrieves all documents uploaded by a specific user.
     *
     * @param username The uploader's username.
     * @return A list of documents belonging to the user.
     */
    public List<Document> getByUsername(String username) {
        log.info("Fetching documents for user: {}", username);
        return documentRepository.findByUsername(username);
    }

    /**
     * Retrieves all documents associated with a specific loan application.
     *
     * @param loanId The loan application identifier.
     * @return A list of documents for the specified loan.
     */
    public List<Document> getByLoanId(String loanId) {
        log.info("Fetching documents for loanId: {}", loanId);
        return documentRepository.findByLoanId(loanId);
    }

    /**
     * Retrieves documents belonging to a user for a specific loan.
     *
     * @param username The uploader's username.
     * @param loanId The loan application identifier.
     * @return A list of documents matching both criteria.
     */
    public List<Document> getByUsernameAndLoanId(String username, String loanId) {
        log.info("Fetching documents for user: {} and loanId: {}", username, loanId);
        return documentRepository.findByUsernameAndLoanId(username, loanId);
    }

    /**
     * Retrieves documents by their current verification status.
     *
     * @param status The status to filter by (e.g., PENDING, VERIFIED, REJECTED).
     * @return A list of documents with the specified status.
     */
    public List<Document> getByVerificationStatus(String status) {
        log.info("Fetching documents with status: {}", status);
        return documentRepository.findByVerificationStatus(status);
    }

    /**
     * Processes an administrative verification decision for a document.
     * Updates the verification status and adds remarks.
     *
     * @param id The ID of the document to verify.
     * @param status The new status (must be either VERIFIED or REJECTED).
     * @param remarks Optional comments explaining the decision.
     * @return The updated Document entity.
     * @throws RuntimeException If an invalid status is provided.
     */
    public Document verifyDocument(Long id, String status, String remarks) {
        log.info("Verifying document ID: {} - status: {}", id, status);

        if (!status.equals("VERIFIED") && !status.equals("REJECTED")) {
            throw new RuntimeException(
                    "Invalid status: [" + status + "]. Must be VERIFIED or REJECTED."
            );
        }

        Document doc = getById(id);

        doc.setVerificationStatus(status);
        doc.setVerifiedRemarks(remarks);

        Document saved = documentRepository.save(doc);
        log.info("Document ID: {} marked as: {}", id, status);
        return saved;
    }

    /**
     * Deletes a document record from the database and removes the associated file from storage.
     *
     * @param id The ID of the document to delete.
     * @return A success message.
     * @throws ResourceNotFoundException If the document ID is invalid.
     */
    public String deleteDocument(Long id) {
        log.info("Deleting document ID: {}", id);

        Document doc = getById(id);

        try {
            Path filePath = Paths.get(doc.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Physical file deleted: {}", doc.getFilePath());
            }
        } catch (Exception e) {
            log.warn("Could not delete physical file: {}", e.getMessage());
        }

        documentRepository.delete(doc);
        log.info("Document ID: {} deleted from DB", id);
        return "Document with ID " + id + " deleted successfully.";
    }
}