package com.finflow.application.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_seq_gen")
    @SequenceGenerator(name = "loan_seq_gen", sequenceName = "loan_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Double amount;

    // PENDING, APPROVED, REJECTED
    @Column(nullable = false)
    private String status;

    // HOME, CAR, PERSONAL, EDUCATION
    @Column(name = "loan_type")
    private String loanType;

    // Reason for applying
    @Column(name = "purpose")
    private String purpose;

    // Auto-set when loan is submitted
    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    // Admin note when approving or rejecting
    @Column(name = "remarks")
    private String remarks;
}