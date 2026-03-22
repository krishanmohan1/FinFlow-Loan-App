package com.finflow.application.controller;

import com.finflow.application.entity.LoanApplication;
import com.finflow.application.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    // USER
    @PostMapping("/apply")
    public LoanApplication apply(@RequestBody LoanApplication loan) {
        return loanService.apply(loan);
    }

    // USER + ADMIN
    @GetMapping("/all")
    public List<LoanApplication> getAll() {
        return loanService.getAll();
    }

    // USER + ADMIN
    @GetMapping("/{id}")
    public LoanApplication getById(@PathVariable Long id) {
        return loanService.getById(id);
    }

    // ADMIN ONLY
    @PutMapping("/status/{id}")
    public LoanApplication updateStatus(@PathVariable Long id,
                                        @RequestParam String status) {
        return loanService.updateStatus(id, status);
    }

    // ADMIN ONLY
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        loanService.delete(id);
        return "Deleted Successfully";
    }
}