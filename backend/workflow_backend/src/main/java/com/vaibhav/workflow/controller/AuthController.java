package com.vaibhav.workflow.controller;

import com.vaibhav.workflow.dto.LoginRequest;
import com.vaibhav.workflow.dto.LoginResponse;
import com.vaibhav.workflow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}