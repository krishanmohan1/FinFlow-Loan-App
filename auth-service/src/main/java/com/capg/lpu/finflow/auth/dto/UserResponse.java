package com.capg.lpu.finflow.auth.dto;

import com.capg.lpu.finflow.auth.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for securely exposing user information over APIs.
 * Excludes sensitive fields like passwords while providing profile metadata.
 */
@Data
public class UserResponse {

    private Long id;
    private String username;
    private String role;
    private LocalDateTime createdAt;
    private boolean active;

    /**
     * Factory method mapping an internal User entity object to its normalized DTO counter-part.
     *
     * @param user the persistent database entity instance
     * @return a decoupled UserResponse DTO ready for public client consumption
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