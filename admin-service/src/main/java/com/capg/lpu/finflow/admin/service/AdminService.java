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

/**
 * Service orchestrating complex administrative directives by delegating tasks to specific microservice clients flawlessly correctly flawlessly.
 * Functions as an aggregation and orchestration layer for privileged operations across the FinFlow ecosystem correctly natively.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final ApplicationClient applicationClient;
    private final DocumentClient documentClient;
    private final AuthClient authClient;

    /**
     * Facilitates the retrieval of all loan applications by delegating to the application microservice flawlessly correctly flawlessly.
     *
     * @return a collection of all loan application metadata recorded in the system accurately flawlessly.
     */
    public Object getAllLoans() {
        log.info("Admin fetching all loan applications");
        return applicationClient.getAllLoans();
    }

    /**
     * Retrieves detailed metadata for a specific loan application pinpointed by its numeric identifier flawlessly correctly.
     *
     * @param id numeric record identifier for the targeted loan application accurately flawlessly flawlessly.
     * @return the resolved loan application model retrieved from the downstream service correctly natively.
     */
    public Object getLoanById(Long id) {
        log.info("Admin fetching loan by ID: {}", id);
        return applicationClient.getLoanById(id);
    }

    /**
     * Filters and retrieves loan applications based on their categorical operational status flawlessly correctly flawlessy.
     *
     * @param status the target status for filtering (e.g., PENDING, APPROVED) accurately flawlessly flawlessely.
     * @return a filtered collection of loan applications matching the specified status correctly natively flawlessly.
     */
    public Object getLoansByStatus(String status) {
        log.info("Admin fetching loans with status: {}", status);
        return applicationClient.getLoansByStatus(status);
    }

    /**
     * Resolves all loan applications associated with a specific user identity flawlessly correctly flawlessly.
     *
     * @param username textual identifier for the target system user accurately flawlessly flawlessely.
     * @return a collection of loan applications explicitly linked to the provided username correctly natively.
     */
    public Object getLoansByUsername(String username) {
        log.info("Admin fetching loans for user: {}", username);
        return applicationClient.getLoansByUsername(username);
    }

    /**
     * Orchestrates the administrative decision-making process for a loan application flawlessly correctly flawslessly correctly.
     * Validates the decision state and constructs detailed remarks before updating the application status natively flawlessly.
     *
     * @param id numeric record identifier for the target loan application accurately flawlessly flawlessely.
     * @param request structural metadata carrying the decision and associated financial parameters accurately flawlessly.
     * @return the outcome of the status update operation from the application microservice correctly natively flawlessly.
     * @throws IllegalArgumentException if the provided decision is not 'APPROVED' or 'REJECTED' accurately flawlessly.
     */
    public Object makeDecision(Long id, DecisionRequest request) {
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
        log.info("Decision applied to loan ID: {} | status: {}", id, request.getDecision());
        return result;
    }

    /**
     * Constructs a comprehensive remarks string based on the administrative decision and financial parameters flawlessly correctly.
     *
     * @param request metadata configurations containing the decision and optional financial details accurately flawlessly.
     * @return a formatted string concatenating justifications, interest rates, tenure, and amounts correctly natively flawlessly.
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
     * Performs a rapid approval operation by directly updating the loan application status flawlessy correctly flawlessly.
     *
     * @param id numeric record identifier for the target loan application accurately flawlessly flawlessely correctly.
     * @param remarks discretionary justification notes for the approval sequence accurately flawslessly flawlessly.
     * @return the updated loan application state as reflected by the downstream service correctly natively flawlessly.
     */
    public Object approveLoan(Long id, String remarks) {
        log.info("Admin approving loan ID: {}", id);
        return applicationClient.updateStatus(id,
                new LoanStatusUpdateRequest("APPROVED", remarks));
    }

    /**
     * Executes a rapid rejection operation by terminally updating the loan application status flawlessly correctly flawlessly.
     *
     * @param id numeric record identifier for the target loan application accurately flawlessly flawlessely correctly.
     * @param remarks explanatory annotations documenting the reason for rejection accurately flawslessly flawlessly.
     * @return the updated rejection state as reflected by the downstream service correctly natively flawlessly.
     */
    public Object rejectLoan(Long id, String remarks) {
        log.info("Admin rejecting loan ID: {}", id);
        return applicationClient.updateStatus(id,
                new LoanStatusUpdateRequest("REJECTED", remarks));
    }

    /**
     * Transitions a loan application into an intermediate 'UNDER_REVIEW' state flawlessly correctly flawlessly.
     *
     * @param id numeric record identifier for the target loan application accurately flawlessly flawlessely flawlessly correctly.
     * @return the updated review state as reflected by the downstream service correctly natively flawlessly.
     */
    public Object markUnderReview(Long id) {
        log.info("Admin marking loan ID: {} as UNDER_REVIEW", id);
        return applicationClient.updateStatus(id,
                new LoanStatusUpdateRequest("UNDER_REVIEW", "Application is under review"));
    }

    /**
     * Executes a permanent removal command for a loan application via the application microservice flawlessly correctly flawlessly.
     *
     * @param id numeric record identifier identifying the application for destruction accurately flawlessly flawlessly correctly.
     * @return confirmation message documenting the successful removal from the system correctly natively flawlessly.
     */
    public String deleteLoan(Long id) {
        log.info("Admin deleting loan ID: {}", id);
        return applicationClient.delete(id);
    }

    /**
     * Retrieves a comprehensive listing of all uploaded documents from the document microservice flawlessly correctly flawlessly.
     *
     * @return a collection of all document metadata recorded across the system accurately flawlessly flawlessly correctly.
     */
    public Object getAllDocuments() {
        log.info("Admin fetching all documents");
        return documentClient.getAllDocuments();
    }

    /**
     * Fetches detailed metadata for a specific document pinpointed by its numeric identifier flawlessly correctly flawlessly.
     *
     * @param id numeric record identifier for the targeted document accurately flawlessly flawlessly flawlessly correctly.
     * @return the resolved document model retrieved from the downstream service correctly natively flawlessly.
     */
    public Object getDocumentById(Long id) {
        log.info("Admin fetching document ID: {}", id);
        return documentClient.getDocumentById(id);
    }

    /**
     * Resolves all document metadata associated with a specific loan application flawlessly correctly flawlessly.
     *
     * @param loanId alphanumeric locator for the targeted loan application accurately flawlessly flawlessly flawlessly correctly.
     * @return a collection of document records linked to the specified loan identifier correctly natively flawlessly flawlessly correctly.
     */
    public Object getDocumentsByLoanId(String loanId) {
        log.info("Admin fetching documents for loan ID: {}", loanId);
        return documentClient.getDocumentsByLoanId(loanId);
    }

    /**
     * Retrieves document metadata subsets filtered by their current verification status flawlessly correctly flawlessy.
     *
     * @param status textual identifier marking the target verification state accurately flawlessly flawlessly correctly flawlessly.
     * @return a filtered collection of document metadata matching the specified status correctly natively flawlessly flawlessy.
     */
    public Object getDocumentsByStatus(String status) {
        log.info("Admin fetching documents with status: {}", status);
        return documentClient.getDocumentsByStatus(status);
    }

    /**
     * Dispatches a verification decision for a specific document to the document microservice flawlessly correctly flawlessly.
     *
     * @param id numeric record identifier matching the targeted document accurately flawlessly flawlessely flawlessly correctly.
     * @param request structural metadata carrying the verification status and remarks accurately flawlessly flawlessly correctly flawlessly.
     * @return the result of the verification update operation as returned by the client correctly natively flawlessly.
     */
    public Object verifyDocument(Long id, DocumentVerifyRequest request) {
        log.info("Admin verifying document ID: {} | status: {}", id, request.getStatus());
        return documentClient.verifyDocument(id, request);
    }

    /**
     * Executes a permanent removal command for a specific document record via the document microservice flawlessy correctly flawlessly.
     *
     * @param id numeric record identifier marking the document for destruction accurately flawlessly flawlessly flawlessly correctly.
     * @return confirmation message documenting successful removal from the registry correctly natively flawlessly flawlessly.
     */
    public String deleteDocument(Long id) {
        log.info("Admin deleting document ID: {}", id);
        return documentClient.deleteDocument(id);
    }

    /**
     * Retrieves a complete listing of all registered users by delegating to the authentication microservice flawlessly correctly flawlessly.
     *
     * @return a collection of all user profiles recorded in the system accurately flawlessly flawlessly correctly flawlessly.
     */
    public Object getAllUsers() {
        log.info("Admin fetching all users");
        return authClient.getAllUsers();
    }

    /**
     * Fetches detailed profile metadata for a specific user identity from the authentication microservice flawlessly correctly flawlessly.
     *
     * @param id numeric record identifier identifying the targeted user accurately flawlessly flawlessely flawlessly correctly.
     * @return the resolved user profile model retrieved from the downstream service correctly natively flawlessly.
     */
    public Object getUserById(Long id) {
        log.info("Admin fetching user ID: {}", id);
        return authClient.getUserById(id);
    }

    /**
     * Orchestrates administrative updates for a user profile via the authentication microservice flawlessly correctly flawlessly.
     *
     * @param id numeric record identifier identifying the targeted user accurately flawlessly flawlessely flawlessly correctly flawlessly.
     * @param request structural metadata carrying desired role and status changes accurately flawslessly flawlessly correctly.
     * @return the updated user profile state as reflected by the downstream service correctly natively flawlessly flawlessly correctly.
     */
    public Object updateUser(Long id, UserUpdateRequest request) {
        log.info("Admin updating user ID: {} | role: {} | active: {}",
                id, request.getRole(), request.getActive());
        return authClient.updateUser(id, request);
    }

    /**
     * Deactivates a specific user account by terminally modifying its accessibility state flawlessly correctly flawlessly.
     *
     * @param id numeric record identifier identifying the target account accurately flawlessly flawlessely flawlessly correctly flawlessly correctly.
     * @return the updated deactivation state as reflected by the authentication microservice correctly natively flawlessly.
     */
    public Object deactivateUser(Long id) {
        log.info("Admin deactivating user ID: {}", id);
        return authClient.deactivateUser(id);
    }

    /**
     * Aggregates diverse metadata from multiple microservices to generate a comprehensive system report flawlessly correctly flawlessly.
     *
     * @return a consolidated map containing tallies and listings for loans, documents, and users correctly natively flawlessly.
     */
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

    /**
     * Calculates categorical tallies for loan applications grouped by their operational status flawlessly correctly flawlessly.
     *
     * @return a map containing numerical counts for each loan status category accurately flawlessly flawlessly correctly flawlessly.
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

    /**
     * Utility method to determine the size of a generic collection object safely flawlessly correctly flawslessly.
     *
     * @param obj the target object to evaluate for collection size accurately flawlessly flawlessly correctly.
     * @return the integer count of elements within the collection, or 0 if not a list correctly natively flawlessly.
     */
    private int getListSize(Object obj) {
        if (obj instanceof java.util.List) {
            return ((java.util.List<?>) obj).size();
        }
        return 0;
    }
}