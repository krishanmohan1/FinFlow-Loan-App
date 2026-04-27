package com.capg.lpu.finflow.admin.service;

import com.capg.lpu.finflow.admin.client.ApplicationClient;
import com.capg.lpu.finflow.admin.client.AuthClient;
import com.capg.lpu.finflow.admin.client.DocumentClient;
import com.capg.lpu.finflow.admin.dto.DecisionRequest;
import com.capg.lpu.finflow.admin.dto.DocumentVerifyRequest;
import com.capg.lpu.finflow.admin.dto.LoanStatusUpdateRequest;
import com.capg.lpu.finflow.admin.dto.StaffRegistrationRequest;
import com.capg.lpu.finflow.admin.dto.UserUpdateRequest;
import com.capg.lpu.finflow.admin.entity.AdminActionAudit;
import com.capg.lpu.finflow.admin.entity.ReportSnapshot;
import com.capg.lpu.finflow.admin.repository.AdminActionAuditRepository;
import com.capg.lpu.finflow.admin.repository.ReportSnapshotRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for administrative orchestration.
 * Coordinates operations across multiple microservices by delegating to their respective Feign clients.
 * Provides higher-level administrative workflows and data aggregation.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final ApplicationClient applicationClient;
    private final DocumentClient documentClient;
    private final AuthClient authClient;
    private final AdminActionAuditRepository adminActionAuditRepository;
    private final ReportSnapshotRepository reportSnapshotRepository;
    private final ObjectMapper objectMapper;

    /**
     * Retrieves all loan applications from the application microservice.
     *
     * @return A collection of all loan applications.
     */
    public Object getAllLoans() {
        log.info("Admin fetching all loan applications");
        return applicationClient.getAllLoans();
    }

    /**
     * Retrieves a specific loan application by its ID.
     *
     * @param id The unique identifier of the loan application.
     * @return The requested loan application data.
     */
    
    
    public Object getLoanById(Long id) {
        log.info("Admin fetching loan by ID: {}", id);
        return applicationClient.getLoanById(id);
    }

    /**
     * Retrieves loan applications filtered by their current status.
     *
     * @param status The status to filter by (e.g., PENDING, APPROVED).
     * @return A collection of matching loan applications.
     */
    
    public Object getLoansByStatus(String status) {
        log.info("Admin fetching loans with status: {}", status);
        return applicationClient.getLoansByStatus(status);
    }

    /**
     * Retrieves all loan applications associated with a specific username.
     *
     * @param username The username of the applicant.
     * @return A collection of loan applications for the user.
     */
    public Object getLoansByUsername(String username) {
        log.info("Admin fetching loans for user: {}", username);
        return applicationClient.getLoansByUsername(username);
    }

    /**
     * Processes an administrative decision for a loan application.
     * Validates the decision type and constructs a detailed remarks string before updating.
     *
     * @param id The ID of the loan application.
     * @param request The object containing decision details (APPROVED/REJECTED) and financial parameters.
     * @return The response from the application service update.
     * @throws IllegalArgumentException If an invalid decision is provided.
     */
    public Object makeDecision(Long id, DecisionRequest request, String actorUsername) {
        log.info("Admin making decision on loan ID: {} | decision: {}", id, request.getDecision());

        if (!"APPROVED".equals(request.getDecision()) && !"REJECTED".equals(request.getDecision())) {
            throw new IllegalArgumentException("Decision must be APPROVED or REJECTED");
        }

        String fullRemarks = buildRemarks(request);

        LoanStatusUpdateRequest statusRequest = new LoanStatusUpdateRequest(
                request.getDecision(),
                fullRemarks
        );

        Object result = applicationClient.updateStatus(id, statusRequest);
        recordAudit("LOAN_DECISION", "LOAN", String.valueOf(id), actorUsername, "SUCCESS",
                "Decision=" + request.getDecision() + ", remarks=" + fullRemarks);
        log.info("Decision applied to loan ID: {} | status: {}", id, request.getDecision());
        return result;
    }

    /**
     * Composes a detailed remarks string for approved loans, incorporating financial terms.
     *
     * @param request The decision request containing remarks and financial values.
     * @return A formatted string suitable for the application's audit/remarks trail.
     */
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

    /**
     * Quickly approves a loan application.
     *
     * @param id The ID of the loan application to approve.
     * @param remarks Administrative comments for the approval.
     * @return The updated loan application data.
     */
    public Object approveLoan(Long id, String remarks, String actorUsername) {
        log.info("Admin approving loan ID: {}", id);
        Object result = applicationClient.updateStatus(id,
                new LoanStatusUpdateRequest("APPROVED", remarks));
        recordAudit("LOAN_APPROVE", "LOAN", String.valueOf(id), actorUsername, "SUCCESS", remarks);
        return result;
    }

    /**
     * Quickly rejects a loan application.
     *
     * @param id The ID of the loan application to reject.
     * @param remarks The reason for rejection.
     * @return The updated loan application data.
     */
    public Object rejectLoan(Long id, String remarks, String actorUsername) {
        log.info("Admin rejecting loan ID: {}", id);
        Object result = applicationClient.updateStatus(id,
                new LoanStatusUpdateRequest("REJECTED", remarks));
        recordAudit("LOAN_REJECT", "LOAN", String.valueOf(id), actorUsername, "SUCCESS", remarks);
        return result;
    }

    /**
     * Marks a loan application as under review.
     *
     * @param id The ID of the loan application.
     * @return The updated loan application data.
     */
    public Object markUnderReview(Long id, String actorUsername) {
        log.info("Admin marking loan ID: {} as UNDER_REVIEW", id);
        Object result = applicationClient.updateStatus(id,
                new LoanStatusUpdateRequest("UNDER_REVIEW", "Application is under review"));
        recordAudit("LOAN_REVIEW", "LOAN", String.valueOf(id), actorUsername, "SUCCESS", "Application is under review");
        return result;
    }

    /**
     * Permanently deletes a loan application record.
     *
     * @param id The ID of the loan application to delete.
     * @return A confirmation message from the application service.
     */
    public String deleteLoan(Long id, String actorUsername) {
        log.info("Admin deleting loan ID: {}", id);
        String result = applicationClient.delete(id);
        recordAudit("LOAN_DELETE", "LOAN", String.valueOf(id), actorUsername, "SUCCESS", result);
        return result;
    }

    /**
     * Retrieves all document metadata from the document microservice.
     *
     * @return A collection of all document records.
     */
    public Object getAllDocuments() {
        log.info("Admin fetching all documents");
        return documentClient.getAllDocuments();
    }

    /**
     * Retrieves a specific document's metadata by its ID.
     *
     * @param id The unique identifier of the document.
     * @return The document metadata.
     */
    public Object getDocumentById(Long id) {
        log.info("Admin fetching document ID: {}", id);
        return documentClient.getDocumentById(id);
    }

    /**
     * Retrieves all documents associated with a specific loan ID.
     *
     * @param loanId The identifier of the loan application.
     * @return A collection of documents linked to the loan.
     */
    public Object getDocumentsByLoanId(String loanId) {
        log.info("Admin fetching documents for loan ID: {}", loanId);
        return documentClient.getDocumentsByLoanId(loanId);
    }

    /**
     * Retrieves documents filtered by their current verification status.
     *
     * @param status The status to filter by (e.g., PENDING, VERIFIED).
     * @return A collection of matching document metadata.
     */
    public Object getDocumentsByStatus(String status) {
        log.info("Admin fetching documents with status: {}", status);
        return documentClient.getDocumentsByStatus(status);
    }

    /**
     * Records an administrative verification decision for a document.
     *
     * @param id The ID of the document.
     * @param request The status and remarks for verification.
     * @return The updated document metadata.
     */
    public Object verifyDocument(Long id, DocumentVerifyRequest request, String actorUsername) {
        log.info("Admin verifying document ID: {} | status: {}", id, request.getStatus());
        Object result = documentClient.verifyDocument(id, request);
        recordAudit("DOCUMENT_VERIFY", "DOCUMENT", String.valueOf(id), actorUsername, "SUCCESS",
                "Status=" + request.getStatus() + ", remarks=" + request.getRemarks());
        return result;
    }

    /**
     * Permanently deletes a specific document record via the document service.
     *
     * @param id The ID of the document to delete.
     * @return A confirmation message.
     */
    public String deleteDocument(Long id, String actorUsername) {
        log.info("Admin deleting document ID: {}", id);
        String result = documentClient.deleteDocument(id);
        recordAudit("DOCUMENT_DELETE", "DOCUMENT", String.valueOf(id), actorUsername, "SUCCESS", result);
        return result;
    }

    /**
     * Retrieves all registered users from the authentication microservice.
     *
     * @return A collection of all user profiles.
     */
    public Object getAllUsers() {
        log.info("Admin fetching all users");
        return authClient.getAllUsers();
    }

    /**
     * Retrieves a specific user profile by its ID.
     *
     * @param id The unique identifier of the user account.
     * @return The requested user profile data.
     */
    public Object getUserById(Long id) {
        log.info("Admin fetching user ID: {}", id);
        return authClient.getUserById(id);
    }

    /**
     * Creates a new internal admin account for operations users.
     *
     * @param request The staff onboarding request.
     * @return The created user profile.
     */
    public Object registerStaff(StaffRegistrationRequest request, String actorUsername) {
        log.info("Admin onboarding internal staff username: {}", request.getUsername());
        Object result = authClient.registerStaff(request);
        recordAudit("STAFF_REGISTER", "USER", request.getUsername(), actorUsername, "SUCCESS",
                "Internal admin account created");
        return result;
    }

    /**
     * Updates an existing user's profile information.
     *
     * @param id The ID of the user account.
     * @param request Updated profile details and role mapping.
     * @return The updated user profile data.
     */
    public Object updateUser(Long id, UserUpdateRequest request, String actorUsername) {
        log.info("Admin updating user ID: {} | role: {} | active: {}",
                id, request.getRole(), request.getActive());
        Object result = authClient.updateUser(id, request);
        recordAudit("USER_UPDATE", "USER", String.valueOf(id), actorUsername, "SUCCESS",
                "role=" + request.getRole() + ", active=" + request.getActive());
        return result;
    }

    /**
     * Deactivates a specific user account.
     *
     * @param id The ID of the user account to deactivate.
     * @return The updated user profile data.
     */
    public Object deactivateUser(Long id, String actorUsername) {
        log.info("Admin deactivating user ID: {}", id);
        Object result = authClient.deactivateUser(id);
        recordAudit("USER_DEACTIVATE", "USER", String.valueOf(id), actorUsername, "SUCCESS",
                "User deactivated");
        return result;
    }

    /**
     * Aggregates data from multiple services to generate a platform-wide summary report.
     * Includes loan statistics, total documents, and user summaries.
     *
     * @return A map containing aggregated system statistics.
     */
    public Object generateReport(String actorUsername) {
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

        saveReportSnapshot("SUMMARY_REPORT", report);
        recordAudit("REPORT_GENERATE", "REPORT", "SUMMARY_REPORT", actorUsername, "SUCCESS",
                "Summary report generated");
        log.info("Report generated successfully");
        return report;
    }

    /**
     * Provides a breakdown of total loan applications grouped by their current status.
     *
     * @return A map containing numerical counts for each status category.
     */
    public Object getLoanCountByStatus() {
        log.info("Admin fetching loan count by status");

        Map<String, Object> counts = new HashMap<>();
        counts.put("PENDING",      getListSize(applicationClient.getLoansByStatus("PENDING")));
        counts.put("APPROVED",     getListSize(applicationClient.getLoansByStatus("APPROVED")));
        counts.put("REJECTED",     getListSize(applicationClient.getLoansByStatus("REJECTED")));
        counts.put("UNDER_REVIEW", getListSize(applicationClient.getLoansByStatus("UNDER_REVIEW")));

        return counts;
    }

    public java.util.List<AdminActionAudit> getRecentAudits() {
        log.info("Admin fetching recent audit records");
        return adminActionAuditRepository.findTop20ByOrderByCreatedAtDesc();
    }

    public java.util.List<ReportSnapshot> getReportHistory() {
        log.info("Admin fetching report snapshot history");
        return reportSnapshotRepository.findTop10ByOrderByGeneratedAtDesc();
    }

    /**
     * Utility method to safely determine the size of a list-like object.
     *
     * @param obj The object to evaluate.
     * @return The number of elements if it's a List, otherwise 0.
     */
    private int getListSize(Object obj) {
        if (obj instanceof java.util.List) {
            return ((java.util.List<?>) obj).size();
        }
        return 0;
    }

    private void recordAudit(String actionType, String targetType, String targetId,
                             String actorUsername, String outcome, String details) {
        adminActionAuditRepository.save(AdminActionAudit.builder()
                .actionType(actionType)
                .targetType(targetType)
                .targetId(targetId)
                .actorUsername(actorUsername)
                .outcome(outcome)
                .details(details)
                .build());
    }

    private void saveReportSnapshot(String snapshotType, Map<String, Object> payload) {
        try {
            reportSnapshotRepository.save(ReportSnapshot.builder()
                    .snapshotType(snapshotType)
                    .payloadJson(objectMapper.writeValueAsString(payload))
                    .build());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize report snapshot", ex);
        }
    }
}
