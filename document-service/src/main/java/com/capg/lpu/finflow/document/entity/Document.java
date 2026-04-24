package com.capg.lpu.finflow.document.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    @Column(nullable = false, length = 30)
    private String username;

    /**
     * The unique identifier of the loan application this document belongs to.
     */
    
    @NotBlank(message = "Loan ID is required")
    @Size(min = 1, max = 50, message = "Loan ID must be between 1 and 50 characters")
    @Column(nullable = false, length = 50)
    private String loanId;

    /**
     * The type or category of the document (e.g., AADHAAR, PAN, SALARY_SLIP).
     */
    
    @NotBlank(message = "Document type is required")
    @Pattern(
            regexp = "^(AADHAAR|PAN|PASSPORT|SALARY_SLIP|BANK_STATEMENT|PHOTO|ADDRESS_PROOF|INCOME_PROOF|OTHER)$",
            message = "Document type must be a supported document category"
    )
    @Column(nullable = false, length = 30)
    private String documentType;

    /**
     * The original or stored name of the file.
     */
    
    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name must be at most 255 characters")
    @Column(nullable = false, length = 255)
    private String fileName;

    /**
     * The physical or relative path where the file is stored on the server.
     */
    
    @NotBlank(message = "File path is required")
    @Size(max = 500, message = "File path must be at most 500 characters")
    @Column(nullable = false, length = 500)
    private String filePath;

    /**
     * The verification status of the document (PENDING, VERIFIED, REJECTED).
     */
    
    @NotBlank(message = "Verification status is required")
    @Pattern(
            regexp = "^(PENDING|VERIFIED|REJECTED)$",
            message = "Verification status must be PENDING, VERIFIED, or REJECTED"
    )
    @Column(nullable = false, length = 20)
    private String verificationStatus;

    /**
     * Remarks provided by an administrator during the document verification process.
     */
    
    @Size(max = 500, message = "Verification remarks must be at most 500 characters")
    @Column(name = "verified_remarks", length = 500)
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
