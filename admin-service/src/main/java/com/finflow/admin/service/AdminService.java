package com.finflow.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.finflow.admin.client.ApplicationClient;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ApplicationClient applicationClient;

    public String approveLoan(Long id) {
        return applicationClient.updateStatus(id, "APPROVED");
    }

    public String rejectLoan(Long id) {
        return applicationClient.updateStatus(id, "REJECTED");
    }

    public String deleteLoan(Long id) {
        return applicationClient.delete(id);
    }
}