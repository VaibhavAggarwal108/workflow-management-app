package com.vaibhav.workflow.service;

import com.vaibhav.workflow.dto.LoginRequest;
import com.vaibhav.workflow.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}