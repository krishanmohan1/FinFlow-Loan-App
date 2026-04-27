package com.capg.lpu.finflow.auth.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
     * Applicant full legal name used in customer-facing loan workflows.
     */
    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 80, message = "Full name must be between 3 and 80 characters")
    @Column(name = "full_name", nullable = false, length = 80)
    private String fullName;

    /**
     * Primary email address for customer communication and notifications.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 120, message = "Email must be at most 120 characters")
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    /**
     * Mobile number used for onboarding and servicing communications.
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    @Column(name = "phone_number", nullable = false, length = 10)
    private String phoneNumber;

    /**
     * Applicant date of birth for underwriting and identity checks.
     */
    @NotNull(message = "Date of birth is required")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    /**
     * Primary residential address line of the applicant.
     */
    @NotBlank(message = "Address line is required")
    @Size(min = 10, max = 120, message = "Address line must be between 10 and 120 characters")
    @Column(name = "address_line1", nullable = false, length = 120)
    private String addressLine1;

    /**
     * City of residence for servicing and KYC purposes.
     */
    @NotBlank(message = "City is required")
    @Size(min = 2, max = 60, message = "City must be between 2 and 60 characters")
    @Column(nullable = false, length = 60)
    private String city;

    /**
     * State or union territory of residence.
     */
    @NotBlank(message = "State is required")
    @Size(min = 2, max = 60, message = "State must be between 2 and 60 characters")
    @Column(nullable = false, length = 60)
    private String state;

    /**
     * Postal code for operational correspondence.
     */
    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Postal code must be a valid 6-digit PIN")
    @Column(name = "postal_code", nullable = false, length = 6)
    private String postalCode;

    /**
     * Borrower occupation or primary employment descriptor.
     */
    @NotBlank(message = "Occupation is required")
    @Size(min = 2, max = 60, message = "Occupation must be between 2 and 60 characters")
    @Column(nullable = false, length = 60)
    private String occupation;

    /**
     * Declared annual income in INR used for loan suitability and review.
     */
    @NotNull(message = "Annual income is required")
    @PositiveOrZero(message = "Annual income must be zero or positive")
    @Column(name = "annual_income", nullable = false)
    private Double annualIncome;

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
