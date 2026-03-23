package com.finflow.document.entity;

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

    // Username extracted from the RabbitMQ message
    @Column(name = "username")
    private String username;

    // Loan application ID this document is linked to
    @Column(name = "loan_id")
    private String loanId;

    // Event type: LOAN_APPLIED or LOAN_STATUS_UPDATED
    @Column(name = "event_type")
    private String eventType;

    // Full raw message from RabbitMQ
    @Column(name = "content", length = 1000)
    private String content;

    // Auto-set when document record is created
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}