package com.capg.lpu.finflow.application.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The username of the applicant who submitted the loan request.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    @Column(nullable = false, length = 30)
    private String username;

    /**
     * The total amount of the loan requested by the applicant.
     */
    @DecimalMin(value = "1000.00", message = "Loan amount must be at least 1000")
    @Column(nullable = false)
    private Double amount;

    /**
     * The current status of the loan application (e.g., PENDING, APPROVED, REJECTED).
     */
    @NotBlank(message = "Status is required")
    @Pattern(
            regexp = "^(PENDING|UNDER_REVIEW|OFFER_MADE|ACTIVE|REJECTED|WITHDRAWN|OFFER_DECLINED)$",
            message = "Status must be PENDING, UNDER_REVIEW, OFFER_MADE, ACTIVE, REJECTED, WITHDRAWN, or OFFER_DECLINED"
    )
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * The category or purpose classification of the loan (e.g., PERSONAL, MORTGAGE).
     */
    @NotBlank(message = "Loan type is required")
    @Pattern(
            regexp = "^(PERSONAL|HOME|AUTO|EDUCATION|BUSINESS|GOLD)$",
            message = "Loan type must be PERSONAL, HOME, AUTO, EDUCATION, BUSINESS, or GOLD"
    )
    @Column(name = "loan_type", nullable = false, length = 20)
    private String loanType;

    /**
     * Requested repayment tenure in months.
     */
    @jakarta.validation.constraints.Min(value = 6, message = "Tenure must be at least 6 months")
    @jakarta.validation.constraints.Max(value = 360, message = "Tenure must be at most 360 months")
    @Column(name = "tenure_months", nullable = false)
    private Integer tenureMonths;

    /**
     * A description or detailed purpose for why the loan is being requested.
     */
    @NotBlank(message = "Purpose is required")
    @Size(min = 10, max = 255, message = "Purpose must be between 10 and 255 characters")
    @Column(name = "purpose", nullable = false, length = 255)
    private String purpose;

    /**
     * The timestamp when the loan application was submitted.
     */
    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    /**
     * Administrative remarks or justification for decisions made regarding the application.
     */
    @Size(max = 500, message = "Remarks must be at most 500 characters")
    @Column(name = "remarks", length = 500)
    private String remarks;

    /**
     * Final sanctioned principal offered by the credit team.
     */
    @Column(name = "sanctioned_amount")
    private Double sanctionedAmount;

    /**
     * Final offered interest rate.
     */
    @Column(name = "interest_rate")
    private Double interestRate;

    /**
     * Processing fee applied on the sanctioned offer.
     */
    @Column(name = "processing_fee")
    private Double processingFee;

    /**
     * GST amount derived from the processing fee.
     */
    @Column(name = "gst_amount")
    private Double gstAmount;

    /**
     * Computed monthly EMI for the sanctioned offer.
     */
    @Column(name = "monthly_emi")
    private Double monthlyEmi;

    /**
     * First EMI due date offered to the borrower.
     */
    @Column(name = "first_emi_date")
    private LocalDate firstEmiDate;

    /**
     * Borrower response to the current loan offer.
     */
    @Pattern(
            regexp = "^(PENDING|ACCEPTED|DECLINED)$",
            message = "Borrower decision must be PENDING, ACCEPTED, or DECLINED"
    )
    @Column(name = "borrower_decision", length = 20)
    private String borrowerDecision;

    /**
     * Timestamp for the borrower response.
     */
    @Column(name = "borrower_decision_at")
    private LocalDateTime borrowerDecisionAt;

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
        if (this.borrowerDecision == null) {
            this.borrowerDecision = "PENDING";
        }
    }
}
