package com.capg.lpu.finflow.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capg.lpu.finflow.document.entity.Document;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // ✅ Get all documents uploaded by a specific user
    List<Document> findByUsername(String username);

    // ✅ Get all documents linked to a specific loan
    List<Document> findByLoanId(String loanId);

    // ✅ Get all documents of a specific type
    //    e.g. all AADHAAR docs, all PAN docs
    List<Document> findByDocumentType(String documentType);

    // ✅ FIX: NEW — Get all documents by verification status
    //    e.g. ADMIN can fetch all PENDING documents
    List<Document> findByVerificationStatus(String verificationStatus);

    // ✅ FIX: NEW — Check if a document type already exists for a loan
    //    Prevents user from uploading duplicate AADHAAR for same loan
    Optional<Document> findByLoanIdAndDocumentType(String loanId, String documentType);

    // ✅ Get all documents by username and loan ID combined
    List<Document> findByUsernameAndLoanId(String username, String loanId);
}