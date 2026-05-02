package com.capg.lpu.finflow.application.controller;

import com.capg.lpu.finflow.application.dto.LoanApplicationRequest;
import com.capg.lpu.finflow.application.dto.LoanOfferResponseRequest;
import com.capg.lpu.finflow.application.dto.LoanStatusUpdateRequest;
import com.capg.lpu.finflow.application.entity.LoanApplication;
import com.capg.lpu.finflow.application.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    private LoanApplication sampleLoan;

    @BeforeEach
    void setUp() {
        sampleLoan = LoanApplication.builder()
                .id(1L)
                .username("user1")
                .amount(50000.0)
                .loanType("HOME")
                .tenureMonths(120)
                .purpose("Buying a family home.")
                .status("PENDING")
                .build();
    }

    @Test
    void applyAndReadEndpointsDelegateProperly() {
        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setAmount(50000.0);
        request.setLoanType("HOME");
        request.setTenureMonths(120);
        request.setPurpose("Buying a family home.");

        when(loanService.apply(any(LoanApplication.class))).thenReturn(sampleLoan);
        when(loanService.getAll()).thenReturn(List.of(sampleLoan));
        when(loanService.getByUsername("user1")).thenReturn(List.of(sampleLoan));
        when(loanService.getByIdSecure(1L, "user1", "USER")).thenReturn(sampleLoan);
        when(loanService.getByStatus("PENDING")).thenReturn(List.of(sampleLoan));
        when(loanService.getByUsernameAndStatus("user1", "PENDING")).thenReturn(List.of(sampleLoan));

        assertThat(loanController.apply(request, "user1").getBody()).isEqualTo(sampleLoan);
        assertThat(loanController.getAll("admin", "ADMIN").getBody()).containsExactly(sampleLoan);
        assertThat(loanController.getAll("user1", "USER").getBody()).containsExactly(sampleLoan);
        assertThat(loanController.getById(1L, "user1", "USER").getBody()).isEqualTo(sampleLoan);
        assertThat(loanController.getByStatus("PENDING", "admin", "ADMIN").getBody()).containsExactly(sampleLoan);
        assertThat(loanController.getByStatus("PENDING", "user1", "USER").getBody()).containsExactly(sampleLoan);
    }

    @Test
    void adminOnlyEndpointsEnforceSecurity() {
        LoanStatusUpdateRequest updateRequest = new LoanStatusUpdateRequest();

        assertThatThrownBy(() -> loanController.getByUsername("user1", "USER"))
                .isInstanceOf(SecurityException.class);
        assertThatThrownBy(() -> loanController.updateStatus(1L, updateRequest, "USER"))
                .isInstanceOf(SecurityException.class);
        assertThatThrownBy(() -> loanController.delete(1L, "USER"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void mutationEndpointsDelegateProperly() {
        LoanStatusUpdateRequest updateRequest = new LoanStatusUpdateRequest();
        LoanOfferResponseRequest offerResponseRequest = new LoanOfferResponseRequest();
        offerResponseRequest.setBorrowerDecision("ACCEPTED");

        when(loanService.updateStatus(1L, updateRequest)).thenReturn(sampleLoan);
        when(loanService.withdraw(1L, "user1")).thenReturn(sampleLoan);
        when(loanService.respondToOffer(1L, "user1", offerResponseRequest)).thenReturn(sampleLoan);
        when(loanService.delete(1L)).thenReturn("deleted");

        assertThat(loanController.updateStatus(1L, updateRequest, "ADMIN").getBody()).isEqualTo(sampleLoan);
        assertThat(loanController.withdraw(1L, "user1", "USER").getBody()).isEqualTo(sampleLoan);
        assertThat(loanController.respondToOffer(1L, "user1", "USER", offerResponseRequest).getBody()).isEqualTo(sampleLoan);
        assertThat(loanController.delete(1L, "ADMIN").getBody()).isEqualTo("deleted");

        assertThatThrownBy(() -> loanController.withdraw(1L, "admin", "ADMIN"))
                .isInstanceOf(SecurityException.class);
        assertThatThrownBy(() -> loanController.respondToOffer(1L, "admin", "ADMIN", offerResponseRequest))
                .isInstanceOf(SecurityException.class);
        verify(loanService).delete(1L);
    }
}
