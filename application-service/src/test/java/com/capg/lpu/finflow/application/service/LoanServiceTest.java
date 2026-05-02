package com.capg.lpu.finflow.application.service;

import com.capg.lpu.finflow.application.dto.LoanOfferResponseRequest;
import com.capg.lpu.finflow.application.dto.LoanStatusUpdateRequest;
import com.capg.lpu.finflow.application.entity.LoanApplication;
import com.capg.lpu.finflow.application.exception.ResourceNotFoundException;
import com.capg.lpu.finflow.application.producer.LoanProducer;
import com.capg.lpu.finflow.application.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite for LoanService.
 * Validates the core loan application lifecycle, including submission, status updates, and security checks.
 */
@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanProducer loanProducer;

    @InjectMocks
    private LoanService loanService;

    private LoanApplication sampleLoan;

    /**
     * Initializes a sample loan application entity for consistent state across test cases.
     */
    @BeforeEach
    void setUp() {
        sampleLoan = new LoanApplication();
        sampleLoan.setId(101L);
        sampleLoan.setUsername("testuser");
        sampleLoan.setAmount(50000.0);
        sampleLoan.setLoanType("HOME");
        sampleLoan.setTenureMonths(120);
        sampleLoan.setPurpose("Buying a family home in Bengaluru.");
        sampleLoan.setStatus("PENDING");
    }

    /**
     * Verifies that applying for a loan successfully persists the record and triggers an asynchronous notification.
     */
    @Test
    @DisplayName("test apply() - Should save loan successfully and send RabbitMQ message")
    void testApplyLoan() {
        when(loanRepository.save(any(LoanApplication.class))).thenReturn(sampleLoan);

        LoanApplication result = loanService.apply(sampleLoan);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(101L);
        assertThat(result.getUsername()).isEqualTo("testuser");

        verify(loanRepository, times(1)).save(sampleLoan);
        verify(loanProducer, times(1)).sendLoanCreated(any());
    }

    /**
     * Confirms that a user is barred from accessing loan details belonging to another person.
     */
    @Test
    @DisplayName("test getByIdSecure() - Block access if user tries to view someone else's loan")
    void testGetByIdSecure_AccessDenied() {
        when(loanRepository.findById(101L)).thenReturn(Optional.of(sampleLoan));

        assertThatThrownBy(() -> loanService.getByIdSecure(101L, "hacker", "USER"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Access denied");

        verify(loanRepository, times(1)).findById(101L);
    }

    /**
     * Validates that updating the status of a loan correctly modifies the record and triggers a corresponding event.
     */
    @Test
    @DisplayName("test updateStatus() - Should update status, add remarks, and notify via RabbitMQ")
    void testUpdateStatus() {
        LoanStatusUpdateRequest request = new LoanStatusUpdateRequest();
        request.setStatus("OFFER_MADE");
        request.setRemarks("Offer created after all documents were verified.");
        request.setInterestRate(8.5);
        request.setTenureMonths(120);
        request.setSanctionedAmount(48000.0);
        request.setBorrowerDecision("PENDING");

        when(loanRepository.findById(101L)).thenReturn(Optional.of(sampleLoan));
        
        LoanApplication updatedMock = new LoanApplication();
        updatedMock.setId(101L);
        updatedMock.setUsername("testuser");
        updatedMock.setTenureMonths(120);
        updatedMock.setPurpose("Buying a family home in Bengaluru.");
        updatedMock.setStatus("OFFER_MADE");
        updatedMock.setRemarks("Offer created after all documents were verified.");
        updatedMock.setInterestRate(8.5);
        updatedMock.setSanctionedAmount(48000.0);
        updatedMock.setBorrowerDecision("PENDING");
        
        when(loanRepository.save(any(LoanApplication.class))).thenReturn(updatedMock);

        LoanApplication result = loanService.updateStatus(101L, request);

        assertThat(result.getStatus()).isEqualTo("OFFER_MADE");
        assertThat(result.getRemarks()).isEqualTo("Offer created after all documents were verified.");

        verify(loanRepository, times(1)).save(sampleLoan);
        verify(loanProducer, times(1)).sendLoanStatusUpdated(any());
    }

    /**
     * Ensures that attempting to delete a non-existent loan results in a ResourceNotFoundException.
     */
    @Test
    @DisplayName("test delete() - Should throw exception if loan does not exist")
    void testDelete_NotFound() {
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Loan not found with ID: 999");

        verify(loanRepository, never()).delete(any());
    }

    @Test
    @DisplayName("test withdraw() - Should withdraw borrower owned pending loan")
    void testWithdraw_success() {
        when(loanRepository.findById(101L)).thenReturn(Optional.of(sampleLoan));
        when(loanRepository.save(any(LoanApplication.class))).thenAnswer((invocation) -> invocation.getArgument(0));

        LoanApplication result = loanService.withdraw(101L, "testuser");

        assertThat(result.getStatus()).isEqualTo("WITHDRAWN");
        verify(loanProducer).sendLoanStatusUpdated(any());
    }

    @Test
    @DisplayName("test respondToOffer() - Should activate a loan when borrower accepts an offer")
    void testRespondToOffer_acceptsOffer() {
        sampleLoan.setStatus("OFFER_MADE");

        LoanOfferResponseRequest request = new LoanOfferResponseRequest();
        request.setBorrowerDecision("ACCEPTED");
        request.setBorrowerRemarks("The EMI plan works for me.");

        when(loanRepository.findById(101L)).thenReturn(Optional.of(sampleLoan));
        when(loanRepository.save(any(LoanApplication.class))).thenAnswer((invocation) -> invocation.getArgument(0));

        LoanApplication result = loanService.respondToOffer(101L, "testuser", request);

        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getBorrowerDecision()).isEqualTo("ACCEPTED");
        assertThat(result.getFirstEmiDate()).isNotNull();
        verify(loanProducer).sendLoanStatusUpdated(any());
    }
}
