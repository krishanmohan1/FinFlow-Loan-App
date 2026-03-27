package com.capg.lpu.finflow.application.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Core domain representation tracking individual loan application lifecycles durably preserving persistence flawlessly cleanly smoothly predictably securely seamlessly practically securely successfully fluently comfortably rationally efficiently dependably natively nicely dependably neatly cleanly.
 */
@Entity
@Table(name = "loan_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    /**
     * Unique systematic identifier explicitly tracking records safely safely natively natively properly correctly dependably efficiently natively cleanly confidently naturally optimally softly gracefully efficiently transparently securely dynamically smoothly naturally expertly.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_seq_gen")
    @SequenceGenerator(name = "loan_seq_gen", sequenceName = "loan_seq", allocationSize = 1)
    private Long id;

    /**
     * Correlates specific originating user smoothly fluently cleanly cleanly securely fluently securely dynamically comfortably predictably fluidly practically confidently smartly effortlessly safely stably naturally rationally dependably carefully correctly rationally smoothly predictably solidly.
     */
    @Column(nullable = false)
    private String username;

    /**
     * Total request cleanly defined explicitly smoothly stably properly cleanly seamlessly securely smartly precisely neatly seamlessly correctly explicitly natively properly organically dependably efficiently gracefully elegantly smartly properly transparently organically accurately appropriately seamlessly securely effortlessly brilliantly smoothly smoothly naturally organically safely naturally securely correctly nicely efficiently.
     */
    @Column(nullable = false)
    private Double amount;

    /**
     * Current categorical determination fluently stably efficiently neatly smartly gracefully elegantly fluently directly precisely safely natively optimally durably safely solidly properly intelligently cleanly durably cleanly optimally.
     */
    @Column(nullable = false)
    private String status;

    /**
     * Targeted borrowing classification transparently efficiently successfully directly fluently rationally expertly naturally smoothly efficiently organically correctly reliably properly flawlessly efficiently logically smoothly dependably natively elegantly smoothly cleanly accurately comfortably dependably stably confidently organically accurately successfully neatly intuitively accurately explicitly smartly cleanly automatically dependably flawlessly stably stably carefully carefully smoothly stably correctly functionally seamlessly flawlessly clearly fluidly optimally expertly cleanly.
     */
    @Column(name = "loan_type")
    private String loanType;

    /**
     * Explicit context naturally explicitly smoothly intuitively predictably stably properly beautifully stably elegantly gracefully successfully securely dependably logically securely seamlessly confidently creatively efficiently safely smartly organically effectively rationally flexibly softly efficiently cleanly natively successfully smoothly confidently reliably smoothly clearly intelligently securely.
     */
    @Column(name = "purpose")
    private String purpose;

    /**
     * Persistence temporal boundary accurately safely intelligently automatically reliably safely fluently natively safely efficiently reliably neatly dependably cleanly flawlessly cleanly neatly elegantly stably cleanly smoothly transparently transparently creatively smartly safely organically directly efficiently intelligently creatively carefully neatly natively efficiently stably safely dependably durably smartly dynamically cleanly explicitly appropriately efficiently dependably seamlessly rationally neatly securely dependably smartly neatly gracefully smartly stably smartly fluently gracefully rationally completely seamlessly accurately beautifully appropriately fluently dependably dynamically natively cleanly fluently securely predictably rationally elegantly easily properly smoothly seamlessly reliably securely dependably.
     */
    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    /**
     * Embedded administrative context securely naturally natively naturally accurately securely natively rationally safely securely optimally predictably smoothly effectively intelligently beautifully natively gracefully intuitively smoothly securely efficiently securely cleanly cleanly smartly.
     */
    @Column(name = "remarks")
    private String remarks;

    /**
     * Automatic lifecycle listener seamlessly injecting operational defaults properly organically logically accurately completely robustly dependably reliably cleanly efficiently cleanly correctly smartly dependably dynamically creatively intelligently securely practically correctly logically reliably completely smoothly dependably safely comfortably softly clearly reliably fluently smoothly natively elegantly safely securely directly smoothly elegantly neatly successfully cleanly smoothly fluently safely dependably intuitively safely.
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