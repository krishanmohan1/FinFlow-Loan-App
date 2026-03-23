package com.finflow.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    // USER or ADMIN
    @Column(nullable = false)
    private String role;

    // Auto-set at registration time
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Track if account is active
    @Column(nullable = false)
    private boolean active;
}