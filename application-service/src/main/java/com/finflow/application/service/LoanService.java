package com.finflow.application.service;

import com.finflow.application.entity.LoanApplication;
import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.application.repository.LoanRepository;
import com.finflow.application.producer.LoanProducer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanProducer loanProducer;

    public LoanApplication apply(LoanApplication loan) {
        loan.setStatus("PENDING");

        LoanApplication saved = loanRepository.save(loan);

        // 🔥 SEND MESSAGE
        loanProducer.sendMessage("Loan Created for user: " + loan.getUsername());

        return saved;
    }

    public List<LoanApplication> getAll() {
        return loanRepository.findAll();
    }

    public LoanApplication getById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
    }

    public LoanApplication updateStatus(Long id, String status) {
        LoanApplication loan = getById(id);
        loan.setStatus(status);
        return loanRepository.save(loan);
    }

    public void delete(Long id) {
        LoanApplication loan = getById(id);
        loanRepository.delete(loan);
    }
}