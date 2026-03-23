package com.finflow.admin.service;

import com.finflow.admin.client.ApplicationClient;
import com.finflow.admin.dto.LoanStatusUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final ApplicationClient applicationClient;

    // ✅ Approve loan with optional remark
    public Object approveLoan(Long id, String remarks) {
        log.info("✅ Admin approving loan ID: {} | remarks: {}", id, remarks);
        LoanStatusUpdateRequest request = new LoanStatusUpdateRequest("APPROVED", remarks);
        return applicationClient.updateStatus(id, request);
    }

    // ✅ Reject loan with optional remark
    public Object rejectLoan(Long id, String remarks) {
        log.info("❌ Admin rejecting loan ID: {} | remarks: {}", id, remarks);
        LoanStatusUpdateRequest request = new LoanStatusUpdateRequest("REJECTED", remarks);
        return applicationClient.updateStatus(id, request);
    }

    // ✅ Delete loan application
    public String deleteLoan(Long id) {
        log.info("🗑️ Admin deleting loan ID: {}", id);
        return applicationClient.delete(id);
    }

    // ✅ View all loan applications
    public Object getAllLoans() {
        log.info("📋 Admin fetching all loan applications");
        return applicationClient.getAllLoans();
    }

    // ✅ View loan by ID
    public Object getLoanById(Long id) {
        log.info("🔍 Admin fetching loan by ID: {}", id);
        return applicationClient.getLoanById(id);
    }

    // ✅ View loans by status
    public Object getLoansByStatus(String status) {
        log.info("🔎 Admin fetching loans with status: {}", status);
        return applicationClient.getLoansByStatus(status);
    }
}