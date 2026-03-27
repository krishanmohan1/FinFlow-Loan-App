package com.capg.lpu.finflow.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity class representing a user in the authentication system.
 * Maps user profile data and authorization states to the 'users' database table.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Unique system-generated identifier for the user account.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    /**
     * Unique username chosen by the user during registration.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Cryptographically hashed password for secure authentication.
     */
    @Column(nullable = false)
    private String password;

    /**
     * The security role assigned to the user (e.g., ROLE_USER, ROLE_ADMIN).
     */
    @Column(nullable = false)
    private String role;

    /**
     * Timestamp indicating when the user profile was created.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Status flag indicating whether the user account is currently active.
     */
    @Column(nullable = false)
    private boolean active;
}