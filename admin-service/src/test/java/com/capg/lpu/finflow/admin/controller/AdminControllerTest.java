package com.capg.lpu.finflow.admin.controller;

import com.capg.lpu.finflow.admin.dto.DecisionRequest;
import com.capg.lpu.finflow.admin.dto.DocumentVerifyRequest;
import com.capg.lpu.finflow.admin.dto.StaffRegistrationRequest;
import com.capg.lpu.finflow.admin.dto.UserUpdateRequest;
import com.capg.lpu.finflow.admin.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Test
    void allEndpointsDelegateToService() {
        DecisionRequest decisionRequest = new DecisionRequest();
        decisionRequest.setDecision("APPROVED");
        decisionRequest.setRemarks("ok");
        DocumentVerifyRequest verifyRequest = new DocumentVerifyRequest();
        verifyRequest.setStatus("VERIFIED");
        verifyRequest.setRemarks("fine");
        StaffRegistrationRequest staffRequest = new StaffRegistrationRequest();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();

        when(adminService.getAllLoans()).thenReturn(List.of("loan"));
        when(adminService.getLoanById(1L)).thenReturn("loan1");
        when(adminService.getLoansByStatus("PENDING")).thenReturn(List.of("pending"));
        when(adminService.getLoansByUsername("user1")).thenReturn(List.of("userLoan"));
        when(adminService.makeDecision(1L, decisionRequest, "admin")).thenReturn("decision");
        when(adminService.approveLoan(1L, "remarks", "admin")).thenReturn("approved");
        when(adminService.rejectLoan(1L, "remarks", "admin")).thenReturn("rejected");
        when(adminService.markUnderReview(1L, "admin")).thenReturn("review");
        when(adminService.deleteLoan(1L, "admin")).thenReturn("deleted");
        when(adminService.getAllDocuments()).thenReturn(List.of("doc"));
        when(adminService.getDocumentById(1L)).thenReturn("doc1");
        when(adminService.getDocumentsByLoanId("1")).thenReturn(List.of("loanDoc"));
        when(adminService.getDocumentsByStatus("PENDING")).thenReturn(List.of("pendingDoc"));
        when(adminService.verifyDocument(1L, verifyRequest, "admin")).thenReturn("verified");
        when(adminService.deleteDocument(1L, "admin")).thenReturn("docDeleted");
        when(adminService.getAllUsers()).thenReturn(List.of("user"));
        when(adminService.registerStaff(staffRequest, "admin")).thenReturn("staff");
        when(adminService.getUserById(1L)).thenReturn("user1");
        when(adminService.updateUser(1L, userUpdateRequest, "admin")).thenReturn("updatedUser");
        when(adminService.deactivateUser(1L, "admin")).thenReturn("inactive");
        when(adminService.generateReport("admin")).thenReturn("report");
        when(adminService.getLoanCountByStatus()).thenReturn("counts");
        when(adminService.getRecentAudits()).thenReturn(List.of("audit"));
        when(adminService.getReportHistory()).thenReturn(List.of("history"));

        assertThat(adminController.getAllLoans().getBody()).containsExactly("loan");
        assertThat(adminController.getLoanById(1L).getBody()).isEqualTo("loan1");
        assertThat(adminController.getLoansByStatus("PENDING").getBody()).containsExactly("pending");
        assertThat(adminController.getLoansByUsername("user1").getBody()).containsExactly("userLoan");
        assertThat(adminController.makeDecision(1L, "admin", decisionRequest).getBody()).isEqualTo("decision");
        assertThat(adminController.approveLoan(1L, "admin", "remarks").getBody()).isEqualTo("approved");
        assertThat(adminController.rejectLoan(1L, "admin", "remarks").getBody()).isEqualTo("rejected");
        assertThat(adminController.markUnderReview(1L, "admin").getBody()).isEqualTo("review");
        assertThat(adminController.deleteLoan(1L, "admin").getBody()).isEqualTo("deleted");
        assertThat(adminController.getAllDocuments().getBody()).containsExactly("doc");
        assertThat(adminController.getDocumentById(1L).getBody()).isEqualTo("doc1");
        assertThat(adminController.getDocumentsByLoanId("1").getBody()).containsExactly("loanDoc");
        assertThat(adminController.getDocumentsByStatus("PENDING").getBody()).containsExactly("pendingDoc");
        assertThat(adminController.verifyDocument(1L, "admin", verifyRequest).getBody()).isEqualTo("verified");
        assertThat(adminController.deleteDocument(1L, "admin").getBody()).isEqualTo("docDeleted");
        assertThat(adminController.getAllUsers().getBody()).containsExactly("user");
        assertThat(adminController.registerStaff("admin", staffRequest).getBody()).isEqualTo("staff");
        assertThat(adminController.getUserById(1L).getBody()).isEqualTo("user1");
        assertThat(adminController.updateUser(1L, "admin", userUpdateRequest).getBody()).isEqualTo("updatedUser");
        assertThat(adminController.deactivateUser(1L, "admin").getBody()).isEqualTo("inactive");
        assertThat(adminController.generateReport("admin").getBody()).isEqualTo("report");
        assertThat(adminController.getLoanCountByStatus().getBody()).isEqualTo("counts");
        assertThat(adminController.getRecentAudits().getBody()).containsExactly("audit");
        assertThat(adminController.getReportHistory().getBody()).containsExactly("history");
    }
}
