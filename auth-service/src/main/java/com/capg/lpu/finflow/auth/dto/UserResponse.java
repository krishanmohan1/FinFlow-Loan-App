package com.capg.lpu.finflow.auth.dto;

import com.capg.lpu.finflow.auth.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {

    private Long id;
    private String username;
    private String role;
    private LocalDateTime createdAt;
    private boolean active;

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