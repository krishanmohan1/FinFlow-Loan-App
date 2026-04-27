package com.capg.lpu.finflow.admin.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Persistent audit trail for actions initiated by the admin service.
 */
@Entity
@Table(name = "admin_action_audits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminActionAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 60)
    @Column(name = "action_type", nullable = false, length = 60)
    private String actionType;

    @NotBlank
    @Size(max = 80)
    @Column(name = "target_type", nullable = false, length = 80)
    private String targetType;

    @Size(max = 80)
    @Column(name = "target_id", length = 80)
    private String targetId;

    @Size(max = 120)
    @Column(name = "actor_username", length = 120)
    private String actorUsername;

    @Size(max = 30)
    @Column(name = "outcome", nullable = false, length = 30)
    private String outcome;

    @Size(max = 1000)
    @Column(name = "details", length = 1000)
    private String details;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
