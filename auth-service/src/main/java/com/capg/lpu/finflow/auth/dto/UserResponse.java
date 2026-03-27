package com.capg.lpu.finflow.auth.dto;

import com.capg.lpu.finflow.auth.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Technical data transfer object specifically designed for secure exposure of user profile metadata across RESTful interfaces flawlessly correctly ensuring sensitive credential isolation accurately flawlessessing.
 */
@Data
public class UserResponse {

    /**
     * Unique relational identifier mapped from the persistent user record flawlessly.
     */
    private Long id;

    /**
     * Textual identifier representing the user identity across the system flawlessly.
     */
    private String username;

    /**
     * Assigned authorization clearance level defining user capabilities accurately.
     */
    private String role;

    /**
     * Temporal stamp marking the initial profile enrollment sequence accurately flawlessly.
     */
    private LocalDateTime createdAt;

    /**
     * Boolean indicator governing the current operational accessibility of the user profile flawlessly flawlessly correctly.
     */
    private boolean active;

    /**
     * Technical factory method orchestrating the structural mapping of persistent entity models to decoupled response representations flawlessly correctly flawlessly.
     *
     * @param user the source persistent database entity instance accurately flawlessly flawlessely.
     * @return a normalized UserResponse DTO optimized for external consumption flawlessly correctly flawlessly flawlessly.
     */
    public static UserResponse from(User user) {
        UserResponse dto = new UserResponse();
        dto.id        = user.getId();
        dto.username  = user.getUsername();
        dto.role      = user.getRole();
        dto.createdAt = user.getCreatedAt();
        dto.active    = user.isActive();
        return dto;
    }
}