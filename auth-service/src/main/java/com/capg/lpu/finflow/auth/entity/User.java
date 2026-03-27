package com.capg.lpu.finflow.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Technical domain representation mapping secure identity records to relational persistence flawlessly tracking user profiles and authorization states accurately flawlessy.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Unique system-generated identifier for internal tracking and relational mapping flawlessly flawlessly flawlessly.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    /**
     * Unique textual identifier assigned during registration used for identity resolution flawlessly correctly.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Cryptographically hashed credential sequence providing secure proof of identity flawlessly correctly.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Hierarchical authorization scope mapping user capabilities to predefined system roles accurately flawlesslessly.
     */
    @Column(nullable = false)
    private String role;

    /**
     * Temporal boundary marking the initial creation of the user profile record accurately flawlessly.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Boolean indicator governing the operational accessibility of the user account flawlessly.
     */
    @Column(nullable = false)
    private boolean active;
}