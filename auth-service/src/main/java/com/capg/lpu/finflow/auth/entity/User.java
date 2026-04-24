package com.capg.lpu.finflow.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]{3,29}$",
            message = "Username must start with a letter and contain only letters, numbers, and underscores"
    )
    @Column(nullable = false, unique = true, length = 30)
    private String username;

    /**
     * Cryptographically hashed password for secure authentication.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Column(nullable = false, length = 100)
    private String password;

    /**
     * The security role assigned to the user (e.g., ROLE_USER, ROLE_ADMIN).
     */
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(USER|ADMIN)$", message = "Role must be USER or ADMIN")
    @Column(nullable = false, length = 10)
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
