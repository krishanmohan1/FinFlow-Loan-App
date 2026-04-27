package com.capg.lpu.finflow.admin.service;

import com.capg.lpu.finflow.admin.client.ApplicationClient;
import com.capg.lpu.finflow.admin.client.AuthClient;
import com.capg.lpu.finflow.admin.client.DocumentClient;
import com.capg.lpu.finflow.admin.dto.DecisionRequest;
import com.capg.lpu.finflow.admin.dto.LoanStatusUpdateRequest;
import com.capg.lpu.finflow.admin.entity.AdminActionAudit;
import com.capg.lpu.finflow.admin.entity.ReportSnapshot;
import com.capg.lpu.finflow.admin.repository.AdminActionAuditRepository;
import com.capg.lpu.finflow.admin.repository.ReportSnapshotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite for AdminService.
 * Verifies the orchestration of cross-service requests via Feign clients.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private ApplicationClient applicationClient;

    @Mock
    private DocumentClient documentClient;

    @Mock
    private AuthClient authClient;

    @Mock
    private AdminActionAuditRepository adminActionAuditRepository;

    @Mock
    private ReportSnapshotRepository reportSnapshotRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AdminService adminService;

    /**
     * Verifies that the service correctly delegates the request to the application client to fetch all loans.
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
     * Confirms that approval decisions correctly trigger the status update on the application service.
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
        when(adminActionAuditRepository.save(any(AdminActionAudit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Object result = adminService.makeDecision(1L, request, "admin");

        assertThat(result).isEqualTo(expectedResponse);
        
        verify(applicationClient).updateStatus(eq(1L), argThat(statusRequest -> 
            "APPROVED".equals(statusRequest.getStatus()) &&
            statusRequest.getRemarks().contains("8.5%") &&
            statusRequest.getRemarks().contains("50000.0")
        ));
        verify(adminActionAuditRepository).save(any(AdminActionAudit.class));
    }

    /**
     * Ensures that invalid decision types (not APPROVED or REJECTED) are blocked before reaching the application service.
     */
    @Test
    @DisplayName("test makeDecision() - Should block invalid decision text")
    void testMakeDecision_InvalidFail() {
        DecisionRequest request = new DecisionRequest();
        request.setDecision("MAYBE_APPROVED");

        assertThatThrownBy(() -> adminService.makeDecision(1L, request, "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be APPROVED or REJECTED");

        verify(applicationClient, never()).updateStatus(any(), any());
    }

    /**
     * Validates the logic that aggregates loan counts by their respective statuses.
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

    @Test
    @DisplayName("test generateReport() - Should save report snapshot")
    void testGenerateReport_savesSnapshot() throws Exception {
        when(applicationClient.getAllLoans()).thenReturn(List.of("L1"));
        when(applicationClient.getLoansByStatus("PENDING")).thenReturn(List.of());
        when(applicationClient.getLoansByStatus("APPROVED")).thenReturn(List.of());
        when(applicationClient.getLoansByStatus("REJECTED")).thenReturn(List.of());
        when(applicationClient.getLoansByStatus("UNDER_REVIEW")).thenReturn(List.of());
        when(documentClient.getAllDocuments()).thenReturn(List.of("D1"));
        when(authClient.getAllUsers()).thenReturn(List.of("U1"));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"ok\":true}");
        when(reportSnapshotRepository.save(any(ReportSnapshot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(adminActionAuditRepository.save(any(AdminActionAudit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Object result = adminService.generateReport("admin");

        assertThat(result).isInstanceOf(Map.class);
        verify(reportSnapshotRepository).save(any(ReportSnapshot.class));
        verify(adminActionAuditRepository).save(any(AdminActionAudit.class));
    }
}
