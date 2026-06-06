package com.taskflow.dto.request;

import jakarta.validation.constraints.NotNull;

public record LogoutRequest(
        @NotNull(message = "Refresh token is required")
        String refreshToken
) {
}
