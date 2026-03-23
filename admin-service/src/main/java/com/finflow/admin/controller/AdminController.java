package com.finflow.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.finflow.admin.service.AdminService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/approve/{id}")
    public String approve(@PathVariable Long id) {
        return adminService.approveLoan(id);
    }

    @PutMapping("/reject/{id}")
    public String reject(@PathVariable Long id) {
        return adminService.rejectLoan(id);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        return adminService.deleteLoan(id);
    }
}