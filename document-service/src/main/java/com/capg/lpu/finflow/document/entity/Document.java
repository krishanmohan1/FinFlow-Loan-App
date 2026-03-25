package com.capg.lpu.finflow.document.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doc_seq_gen")
    @SequenceGenerator(name = "doc_seq_gen", sequenceName = "doc_seq", allocationSize = 1)
    private Long id;

    // ✅ Username of the applicant who uploaded this document
    @Column(nullable = false)
    private String username;

    // ✅ Links document to a specific loan application
    @Column(nullable = false)
    private String loanId;

    // ✅ AADHAAR, PAN, SALARY_SLIP, BANK_STATEMENT etc.
    @Column(nullable = false)
    private String documentType;

    // ✅ Original file name stored on disk
    @Column(nullable = false)
    private String fileName;

    // ✅ Full path where file is stored on server
    @Column(nullable = false)
    private String filePath;

    // ✅ PENDING, VERIFIED, REJECTED
    @Column(nullable = false)
    private String verificationStatus;

    // ✅ Admin note when verifying or rejecting
    @Column(name = "verified_remarks")
    private String verifiedRemarks;

    // ✅ Auto-set when document is first uploaded
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ✅ FIX: NEW — tracks when admin verifies or rejects
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ✅ FIX: Auto-set createdAt and default verificationStatus
    //         so they are NEVER null in the database
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

    // ✅ FIX: Auto-update updatedAt every time record is updated
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}