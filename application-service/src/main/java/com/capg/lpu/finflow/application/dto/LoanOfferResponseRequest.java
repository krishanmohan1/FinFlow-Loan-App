package com.capg.lpu.finflow.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request model for borrower acceptance or rejection of an admin-issued loan offer.
 */
@Data
public class LoanOfferResponseRequest {

    @NotBlank(message = "Borrower decision is required")
    @Pattern(regexp = "^(ACCEPTED|DECLINED)$", message = "Decision must be ACCEPTED or DECLINED")
    private String borrowerDecision;

    @Size(max = 500, message = "Borrower remarks must be at most 500 characters")
    private String borrowerRemarks;
}
