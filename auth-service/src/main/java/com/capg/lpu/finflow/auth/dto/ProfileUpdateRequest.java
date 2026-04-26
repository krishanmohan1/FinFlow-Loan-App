package com.capg.lpu.finflow.auth.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request model for borrower self-service profile updates.
 * Exposes customer-facing profile fields without security role mutation.
 */
@Data
public class ProfileUpdateRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 80, message = "Full name must be between 3 and 80 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 120, message = "Email must be at most 120 characters")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phoneNumber;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Address line is required")
    @Size(min = 10, max = 120, message = "Address line must be between 10 and 120 characters")
    private String addressLine1;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 60, message = "City must be between 2 and 60 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 60, message = "State must be between 2 and 60 characters")
    private String state;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Postal code must be a valid 6-digit PIN")
    private String postalCode;

    @NotBlank(message = "Occupation is required")
    @Size(min = 2, max = 60, message = "Occupation must be between 2 and 60 characters")
    private String occupation;

    @NotNull(message = "Annual income is required")
    @PositiveOrZero(message = "Annual income must be zero or positive")
    private Double annualIncome;
}
