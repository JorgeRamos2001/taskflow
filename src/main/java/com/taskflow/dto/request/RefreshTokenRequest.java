package com.taskflow.dto.request;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequest(
        @NotNull(message = "Refresh token is required")
        String refreshToken
) {
}
