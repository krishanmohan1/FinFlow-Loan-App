package com.capg.lpu.finflow.document.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity class representing a document uploaded to the system.
 * Stores metadata about the file, its association with a loan application, and its verification status.
 */
@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    /**
     * Unique identifier for the document record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doc_seq_gen")
    @SequenceGenerator(name = "doc_seq_gen", sequenceName = "doc_seq", allocationSize = 1)
    private Long id;

    /**
     * The username of the user who uploaded the document.
     */
    @Column(nullable = false)
    private String username;

    /**
     * The unique identifier of the loan application this document belongs to.
     */
    @Column(nullable = false)
    private String loanId;

    /**
     * The type or category of the document (e.g., AADHAAR, PAN, SALARY_SLIP).
     */
    @Column(nullable = false)
    private String documentType;

    /**
     * The original or stored name of the file.
     */
    @Column(nullable = false)
    private String fileName;

    /**
     * The physical or relative path where the file is stored on the server.
     */
    @Column(nullable = false)
    private String filePath;

    /**
     * The verification status of the document (PENDING, VERIFIED, REJECTED).
     */
    @Column(nullable = false)
    private String verificationStatus;

    /**
     * Remarks provided by an administrator during the document verification process.
     */
    @Column(name = "verified_remarks")
    private String verifiedRemarks;

    /**
     * The timestamp indicating when the document was initially uploaded.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * The timestamp indicating the last time the document record was updated.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Lifecycle callback to initialize default values and timestamps before persisting a new record.
     */
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.verificationStatus == null) {
            this.verificationStatus = "PENDING";
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Lifecycle callback to update the timestamp before an existing record is updated.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}