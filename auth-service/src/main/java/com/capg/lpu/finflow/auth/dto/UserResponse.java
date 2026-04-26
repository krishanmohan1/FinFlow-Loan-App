package com.capg.lpu.finflow.auth.dto;

import com.capg.lpu.finflow.auth.entity.User;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for exposing user profile information.
 * Ensures sensitive data like passwords are excluded from the response.
 */
@Data
public class UserResponse {

    /**
     * Unique identifier for the user account.
     */
    private Long id;

    /**
     * The unique username of the user.
     */
    private String username;

    /**
     * Full display name of the borrower or staff user.
     */
    private String fullName;

    /**
     * Email address used for communications.
     */
    private String email;

    /**
     * Phone number used for contact.
     */
    private String phoneNumber;

    /**
     * Date of birth of the applicant.
     */
    private LocalDate dateOfBirth;

    /**
     * Address line of residence.
     */
    private String addressLine1;

    /**
     * City of residence.
     */
    private String city;

    /**
     * State of residence.
     */
    private String state;

    /**
     * Postal code.
     */
    private String postalCode;

    /**
     * Applicant occupation.
     */
    private String occupation;

    /**
     * Declared annual income.
     */
    private Double annualIncome;

    /**
     * The security role assigned to the user.
     */
    private String role;

    /**
     * Timestamp indicating when the user profile was created.
     */
    private LocalDateTime createdAt;

    /**
     * Status flag indicating whether the user account is active.
     */
    private boolean active;

    /**
     * Static factory method to map a User entity to a UserResponse DTO.
     *
     * @param user The source User entity.
     * @return A populated UserResponse object for external use.
     */
    public static UserResponse from(User user) {
        UserResponse dto = new UserResponse();
        dto.id        = user.getId();
        dto.username  = user.getUsername();
        dto.fullName  = user.getFullName();
        dto.email     = user.getEmail();
        dto.phoneNumber = user.getPhoneNumber();
        dto.dateOfBirth = user.getDateOfBirth();
        dto.addressLine1 = user.getAddressLine1();
        dto.city = user.getCity();
        dto.state = user.getState();
        dto.postalCode = user.getPostalCode();
        dto.occupation = user.getOccupation();
        dto.annualIncome = user.getAnnualIncome();
        dto.role      = user.getRole();
        dto.createdAt = user.getCreatedAt();
        dto.active    = user.isActive();
        return dto;
    }
}
