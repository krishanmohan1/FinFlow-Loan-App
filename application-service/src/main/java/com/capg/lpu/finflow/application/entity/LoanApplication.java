package com.capg.lpu.finflow.application.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity class representing a loan application in the system.
 * Contains core application details including amount, type, and current status.
 */
@Entity
@Table(name = "loan_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    /**
     * Unique identifier for the loan application record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_seq_gen")
    @SequenceGenerator(name = "loan_seq_gen", sequenceName = "loan_seq", allocationSize = 1)
    private Long id;

    /**
     * The username of the applicant who submitted the loan request.
     */
    @Column(nullable = false)
    private String username;

    /**
     * The total amount of the loan requested by the applicant.
     */
    @Column(nullable = false)
    private Double amount;

    /**
     * The current status of the loan application (e.g., PENDING, APPROVED, REJECTED).
     */
    @Column(nullable = false)
    private String status;

    /**
     * The category or purpose classification of the loan (e.g., PERSONAL, MORTGAGE).
     */
    @Column(name = "loan_type")
    private String loanType;

    /**
     * A description or detailed purpose for why the loan is being requested.
     */
    @Column(name = "purpose")
    private String purpose;

    /**
     * The timestamp when the loan application was submitted.
     */
    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    /**
     * Administrative remarks or justification for decisions made regarding the application.
     */
    @Column(name = "remarks")
    private String remarks;

    /**
     * Lifecycle callback to initialize default values before persistence.
     * Sets the application timestamp and defaults the status to PENDING if not specified.
     */
    @PrePersist
    public void prePersist() {
        if (this.appliedAt == null) {
            this.appliedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "PENDING";
        }
    }
}