package com.capg.lpu.finflow.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capg.lpu.finflow.document.entity.Document;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Document entities.
 * Provides abstraction for database operations related to document metadata storage and retrieval.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Retrieves all documents uploaded by a specific user.
     *
     * @param username The username of the uploader.
     * @return A list of documents belonging to the user.
     */
    List<Document> findByUsername(String username);

    /**
     * Retrieves all documents associated with a specific loan ID.
     *
     * @param loanId The identifier of the loan application.
     * @return A list of documents linked to the loan.
     */
    List<Document> findByLoanId(String loanId);

    /**
     * Retrieves documents filtered by their specific type (e.g., PAN, AADHAAR).
     *
     * @param documentType The category of document to search for.
     * @return A list of documents matching the specified type.
     */
    List<Document> findByDocumentType(String documentType);

    /**
     * Retrieves documents based on their current verification status.
     *
     * @param verificationStatus The status to filter by (e.g., PENDING, VERIFIED).
     * @return A list of documents matching the status.
     */
    List<Document> findByVerificationStatus(String verificationStatus);

    /**
     * Finds a specific document for a loan application based on its type.
     * Used for checking if a document of a certain type has already been uploaded for a loan.
     *
     * @param loanId The identifier of the loan application.
     * @param documentType The category of document.
     * @return An Optional containing the document if found.
     */
    Optional<Document> findByLoanIdAndDocumentType(String loanId, String documentType);

    /**
     * Retrieves documents belonging to a specific user and loan application.
     *
     * @param username The username of the uploader.
     * @param loanId The identifier of the loan application.
     * @return A list of documents matching both criteria.
     */
    List<Document> findByUsernameAndLoanId(String username, String loanId);
}