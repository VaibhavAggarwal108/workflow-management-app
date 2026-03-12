package com.vaibhav.workflow.service;

import com.vaibhav.workflow.dto.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
}