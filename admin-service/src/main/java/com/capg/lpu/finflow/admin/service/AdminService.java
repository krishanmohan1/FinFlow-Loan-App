package com.capg.lpu.finflow.admin.service;

import com.capg.lpu.finflow.admin.client.ApplicationClient;
import com.capg.lpu.finflow.admin.client.AuthClient;
import com.capg.lpu.finflow.admin.client.DocumentClient;
import com.capg.lpu.finflow.admin.dto.DecisionRequest;
import com.capg.lpu.finflow.admin.dto.DocumentVerifyRequest;
import com.capg.lpu.finflow.admin.dto.LoanStatusUpdateRequest;
import com.capg.lpu.finflow.admin.dto.UserUpdateRequest;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final ApplicationClient applicationClient;
    private final DocumentClient documentClient;
    private final AuthClient authClient;

    // ================================================================
    // LOAN APPLICATION MANAGEMENT
    // ================================================================

    public Object getAllLoans() {
        log.info("Admin fetching all loan applications");
        return applicationClient.getAllLoans();
    }

    public Object getLoanById(Long id) {
        log.info("Admin fetching loan by ID: {}", id);
        return applicationClient.getLoanById(id);
    }

    public Object getLoansByStatus(String status) {
        log.info("Admin fetching loans with status: {}", status);
        return applicationClient.getLoansByStatus(status);
    }

    public Object getLoansByUsername(String username) {
        log.info("Admin fetching loans for user: {}", username);
        return applicationClient.getLoansByUsername(username);
    }

    // ================================================================
    // LOAN DECISION — APPROVE / REJECT WITH FULL TERMS
    // ================================================================

    public Object makeDecision(Long id, DecisionRequest request) {
        log.info("Admin making decision on loan ID: {} | decision: {}", id, request.getDecision());

        // ✅ Validate decision value
        if (!"APPROVED".equals(request.getDecision()) && !"REJECTED".equals(request.getDecision())) {
            throw new IllegalArgumentException("Decision must be APPROVED or REJECTED");
        }

        // ✅ Build full remarks string including loan terms if approved
        String fullRemarks = buildRemarks(request);

        LoanStatusUpdateRequest statusRequest = new LoanStatusUpdateRequest(
                request.getDecision(),
                fullRemarks
        );

        Object result = applicationClient.updateStatus(id, statusRequest);
        log.info("Decision applied to loan ID: {} | status: {}", id, request.getDecision());
        return result;
    }

    // ✅ Builds a detailed remark including loan terms for approved loans
    private String buildRemarks(DecisionRequest request) {
        if ("APPROVED".equals(request.getDecision())) {
            StringBuilder sb = new StringBuilder();
            if (request.getRemarks() != null) {
                sb.append(request.getRemarks());
            }
            if (request.getInterestRate() != null) {
                sb.append(" | Interest Rate: ").append(request.getInterestRate()).append("%");
            }
            if (request.getTenureMonths() != null) {
                sb.append(" | Tenure: ").append(request.getTenureMonths()).append(" months");
            }
            if (request.getSanctionedAmount() != null) {
                sb.append(" | Sanctioned Amount: ").append(request.getSanctionedAmount());
            }
            return sb.toString();
        }
        return request.getRemarks();
    }

    // ✅ Quick approve — just a status + remark
    public Object approveLoan(Long id, String remarks) {
        log.info("Admin approving loan ID: {}", id);
        return applicationClient.updateStatus(id,
                new LoanStatusUpdateRequest("APPROVED", remarks));
    }

    // ✅ Quick reject — just a status + remark
    public Object rejectLoan(Long id, String remarks) {
        log.info("Admin rejecting loan ID: {}", id);
        return applicationClient.updateStatus(id,
                new LoanStatusUpdateRequest("REJECTED", remarks));
    }

    // ✅ Move loan to Under Review status
    public Object markUnderReview(Long id) {
        log.info("Admin marking loan ID: {} as UNDER_REVIEW", id);
        return applicationClient.updateStatus(id,
                new LoanStatusUpdateRequest("UNDER_REVIEW", "Application is under review"));
    }

    // ✅ Permanently delete a loan application
    public String deleteLoan(Long id) {
        log.info("Admin deleting loan ID: {}", id);
        return applicationClient.delete(id);
    }

    // ================================================================
    // DOCUMENT VERIFICATION
    // ================================================================

    public Object getAllDocuments() {
        log.info("Admin fetching all documents");
        return documentClient.getAllDocuments();
    }

    public Object getDocumentById(Long id) {
        log.info("Admin fetching document ID: {}", id);
        return documentClient.getDocumentById(id);
    }

    public Object getDocumentsByLoanId(String loanId) {
        log.info("Admin fetching documents for loan ID: {}", loanId);
        return documentClient.getDocumentsByLoanId(loanId);
    }

    // ✅ FIX: Replaced getDocumentsByUsername with getDocumentsByStatus
    //         Document Service has no /document/user/{username} endpoint for ADMIN
    //         Use /document/status/{status} instead (PENDING, VERIFIED, REJECTED)
    public Object getDocumentsByStatus(String status) {
        log.info("Admin fetching documents with status: {}", status);
        return documentClient.getDocumentsByStatus(status);
    }

    public Object verifyDocument(Long id, DocumentVerifyRequest request) {
        log.info("Admin verifying document ID: {} | status: {}", id, request.getStatus());
        return documentClient.verifyDocument(id, request);
    }

    // ✅ NEW: Delete document support
    public String deleteDocument(Long id) {
        log.info("Admin deleting document ID: {}", id);
        return documentClient.deleteDocument(id);
    }

    // ================================================================
    // USER MANAGEMENT
    // ================================================================

    public Object getAllUsers() {
        log.info("Admin fetching all users");
        return authClient.getAllUsers();
    }

    public Object getUserById(Long id) {
        log.info("Admin fetching user ID: {}", id);
        return authClient.getUserById(id);
    }

    public Object updateUser(Long id, UserUpdateRequest request) {
        log.info("Admin updating user ID: {} | role: {} | active: {}",
                id, request.getRole(), request.getActive());
        return authClient.updateUser(id, request);
    }

    public Object deactivateUser(Long id) {
        log.info("Admin deactivating user ID: {}", id);
        return authClient.deactivateUser(id);
    }

    // ================================================================
    // REPORTS
    // ================================================================

    public Object generateReport() {
        log.info("Admin generating summary report");

        Map<String, Object> report = new HashMap<>();
        report.put("allLoans",         applicationClient.getAllLoans());
        report.put("pendingLoans",     applicationClient.getLoansByStatus("PENDING"));
        report.put("approvedLoans",    applicationClient.getLoansByStatus("APPROVED"));
        report.put("rejectedLoans",    applicationClient.getLoansByStatus("REJECTED"));
        report.put("underReviewLoans", applicationClient.getLoansByStatus("UNDER_REVIEW"));
        report.put("allDocuments",     documentClient.getAllDocuments());
        report.put("allUsers",         authClient.getAllUsers());
        report.put("generatedAt",      java.time.LocalDateTime.now().toString());

        log.info("Report generated successfully");
        return report;
    }

    public Object getLoanCountByStatus() {
        log.info("Admin fetching loan count by status");

        Map<String, Object> counts = new HashMap<>();
        counts.put("PENDING",      getListSize(applicationClient.getLoansByStatus("PENDING")));
        counts.put("APPROVED",     getListSize(applicationClient.getLoansByStatus("APPROVED")));
        counts.put("REJECTED",     getListSize(applicationClient.getLoansByStatus("REJECTED")));
        counts.put("UNDER_REVIEW", getListSize(applicationClient.getLoansByStatus("UNDER_REVIEW")));

        return counts;
    }

    // ✅ Safe helper — handles null or non-list responses
    private int getListSize(Object obj) {
        if (obj instanceof java.util.List) {
            return ((java.util.List<?>) obj).size();
        }
        return 0;
    }
}