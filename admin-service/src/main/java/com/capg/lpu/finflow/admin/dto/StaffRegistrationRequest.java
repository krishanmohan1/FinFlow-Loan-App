package com.capg.lpu.finflow.admin.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request body used by admins to onboard a new internal staff account.
 */
@Data
public class StaffRegistrationRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 80, message = "Full name must be between 3 and 80 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 120, message = "Email must be 120 characters or fewer")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phoneNumber;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 120, message = "Address must be between 10 and 120 characters")
    private String addressLine1;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 60, message = "City must be between 2 and 60 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 60, message = "State must be between 2 and 60 characters")
    private String state;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Postal code must be a valid 6-digit PIN code")
    private String postalCode;

    @NotBlank(message = "Occupation is required")
    @Size(min = 2, max = 60, message = "Occupation must be between 2 and 60 characters")
    private String occupation;

    @NotNull(message = "Annual income is required")
    @Min(value = 0, message = "Annual income cannot be negative")
    @Max(value = 1000000000, message = "Annual income is unrealistically high")
    private Double annualIncome;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]{3,29}$", message = "Username must start with a letter and contain only letters, numbers, and underscores")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    private String password;
}
