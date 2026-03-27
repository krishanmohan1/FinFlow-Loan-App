package com.capg.lpu.finflow.admin.service;

import com.capg.lpu.finflow.admin.client.ApplicationClient;
import com.capg.lpu.finflow.admin.client.AuthClient;
import com.capg.lpu.finflow.admin.client.DocumentClient;
import com.capg.lpu.finflow.admin.dto.DecisionRequest;
import com.capg.lpu.finflow.admin.dto.LoanStatusUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite designated for verifying Administrative delegation logic properly orchestrates distributed microservice clients safely.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private ApplicationClient applicationClient;

    @Mock
    private DocumentClient documentClient;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private AdminService adminService;

    /**
     * Asserts proper proxy delegation successfully extracting widespread list outputs directly reliably naturally.
     */
    @Test
    @DisplayName("test getAllLoans() - Should call ApplicationClient")
    void testGetAllLoans() {
        Object mockResponse = Arrays.asList("Loan1", "Loan2");
        when(applicationClient.getAllLoans()).thenReturn(mockResponse);

        Object result = adminService.getAllLoans();

        assertThat(result).isEqualTo(mockResponse);
        verify(applicationClient, times(1)).getAllLoans();
    }

    /**
     * Validates conditional mapping sequences appropriately generating explicitly detailed administrative notes seamlessly.
     */
    @Test
    @DisplayName("test makeDecision() - Should successfully format approval remarks")
    void testMakeDecision_Approve() {
        DecisionRequest request = new DecisionRequest();
        request.setDecision("APPROVED");
        request.setRemarks("Looking good");
        request.setInterestRate(8.5);
        request.setTenureMonths(24);
        request.setSanctionedAmount(50000.0);

        Object expectedResponse = "Success";
        when(applicationClient.updateStatus(eq(1L), any(LoanStatusUpdateRequest.class)))
                .thenReturn(expectedResponse);

        Object result = adminService.makeDecision(1L, request);

        assertThat(result).isEqualTo(expectedResponse);
        
        verify(applicationClient).updateStatus(eq(1L), argThat(statusRequest -> 
            "APPROVED".equals(statusRequest.getStatus()) &&
            statusRequest.getRemarks().contains("8.5%") &&
            statusRequest.getRemarks().contains("50000.0")
        ));
    }

    /**
     * Prevents false parsing transitions intercepting fundamentally misconfigured status declarations reliably securely.
     */
    @Test
    @DisplayName("test makeDecision() - Should block invalid decision text")
    void testMakeDecision_InvalidFail() {
        DecisionRequest request = new DecisionRequest();
        request.setDecision("MAYBE_APPROVED");

        assertThatThrownBy(() -> adminService.makeDecision(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be APPROVED or REJECTED");

        verify(applicationClient, never()).updateStatus(any(), any());
    }

    /**
     * Evaluates list size computational tracking validating array extractions correctly dynamically mapping outputs intelligently stably dependably naturally smartly.
     */
    @Test
    @DisplayName("test getLoanCountByStatus() - Should accurately retrieve and count lists via Feign Clients")
    void testGetLoanCountByStatus() {
        List<String> mockPendingList = Arrays.asList("L1", "L2", "L3");
        List<String> mockApprovedList = Arrays.asList("L4");

        when(applicationClient.getLoansByStatus("PENDING")).thenReturn(mockPendingList);
        when(applicationClient.getLoansByStatus("APPROVED")).thenReturn(mockApprovedList);
        when(applicationClient.getLoansByStatus("REJECTED")).thenReturn(null);
        when(applicationClient.getLoansByStatus("UNDER_REVIEW")).thenReturn(null);

        Map<String, Object> counts = (Map<String, Object>) adminService.getLoanCountByStatus();

        assertThat(counts.get("PENDING")).isEqualTo(3);
        assertThat(counts.get("APPROVED")).isEqualTo(1);
        assertThat(counts.get("REJECTED")).isEqualTo(0);
        assertThat(counts.get("UNDER_REVIEW")).isEqualTo(0);
    }
}
