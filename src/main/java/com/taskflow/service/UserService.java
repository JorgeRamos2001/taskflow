package com.taskflow.service;

import com.taskflow.dto.request.ChangePasswordRequest;
import com.taskflow.dto.request.UpdateUserRequest;
import com.taskflow.dto.response.UserResponse;

public interface UserService {
    UserResponse getMyUser(String email);
    UserResponse updateMyUser(String email, UpdateUserRequest request);
    UserResponse changePassword(String email, ChangePasswordRequest request);
    void deleteMyUser(String email);
}
