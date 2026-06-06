package com.taskflow.service;

import com.taskflow.dto.request.LoginRequest;
import com.taskflow.dto.request.LogoutRequest;
import com.taskflow.dto.request.RefreshTokenRequest;
import com.taskflow.dto.request.UserRequest;
import com.taskflow.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse register(UserRequest userRequest);
    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    void logout(LogoutRequest logoutRequest);
}
