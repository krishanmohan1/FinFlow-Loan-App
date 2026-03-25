// UserUpdateRequest.java
package com.capg.lpu.finflow.admin.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String role;      // USER or ADMIN
    private Boolean active;   // true = active, false = deactivated
}