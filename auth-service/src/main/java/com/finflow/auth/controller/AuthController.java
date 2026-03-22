package com.finflow.auth.controller;

import com.finflow.auth.entity.User;
import com.finflow.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**************************************************
     * Testing Controller used During Development
     * 
     **************************************************/
    @GetMapping("/test")
    public String test() {
        return "Auth Service Running";
    }
    
    @GetMapping("/admin/test")
    public String adminTest() {
        return "Admin Access Granted";
    }

    @GetMapping("/user/test")
    public String userTest() {
        return "User Access Granted";
    }
    
    /**********************************************************/
    
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return authService.login(user.getUsername(), user.getPassword());
    }
}