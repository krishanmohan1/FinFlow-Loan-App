package com.capg.lpu.finflow.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Persisted entity representing a registered system user.
 * Defines the database schema constraints and holds authentication status data.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    /**
     * Defines the authorization boundary of the user (e.g., USER or ADMIN).
     */
    @Column(nullable = false)
    private String role;

    /**
     * Stamped automatically sequentially upon initial profile creation.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Tracks authorization accessibility. If false, the account is disabled.
     */
    @Column(nullable = false)
    private boolean active;
}