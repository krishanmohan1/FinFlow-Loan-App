package com.capg.lpu.finflow.application.service;

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
 * Technical unit testing suite validating core loan orchestration logic via Mockito-driven dependency isolation flawlessly correctly reliably smoothly natively.
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
     * Initializes test fixtures establishing stable baseline state for subsequent behavioral validations flawlessly smoothly efficiently.
     */
    @BeforeEach
    void setUp() {
        sampleLoan = new LoanApplication();
        sampleLoan.setId(101L);
        sampleLoan.setUsername("testuser");
        sampleLoan.setAmount(50000.0);
        sampleLoan.setLoanType("HOME");
        sampleLoan.setStatus("PENDING");
    }

    /**
     * Validates that application submission correctly persists records and triggers remote notifications flawlessly correctly smoothly fluently.
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
        verify(loanProducer, times(1)).sendMessage(anyString());
    }

    /**
     * Verifies that unauthorized access attempts to private loan records are strictly intercepted and blocked flawlessly durably securely.
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
     * Confirms that administrative status updates are properly reflected in state and broadcasted to integration layers flawlessly correctly fluently.
     */
    @Test
    @DisplayName("test updateStatus() - Should update status, add remarks, and notify via RabbitMQ")
    void testUpdateStatus() {
        LoanStatusUpdateRequest request = new LoanStatusUpdateRequest();
        request.setStatus("APPROVED");
        request.setRemarks("All documents verified ok.");

        when(loanRepository.findById(101L)).thenReturn(Optional.of(sampleLoan));
        
        LoanApplication updatedMock = new LoanApplication();
        updatedMock.setId(101L);
        updatedMock.setUsername("testuser");
        updatedMock.setStatus("APPROVED");
        updatedMock.setRemarks("All documents verified ok.");
        
        when(loanRepository.save(any(LoanApplication.class))).thenReturn(updatedMock);

        LoanApplication result = loanService.updateStatus(101L, request);

        assertThat(result.getStatus()).isEqualTo("APPROVED");
        assertThat(result.getRemarks()).isEqualTo("All documents verified ok.");

        verify(loanRepository, times(1)).save(sampleLoan);
        verify(loanProducer, times(1)).sendMessage(contains("APPROVED"));
    }

    /**
     * Ensures that delete operations correctly identify absent targets and signal errors appropriately flawlessly correctly fluently.
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
}
