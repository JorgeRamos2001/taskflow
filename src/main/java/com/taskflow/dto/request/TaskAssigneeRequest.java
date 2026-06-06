package com.taskflow.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TaskAssigneeRequest(
        @NotNull(message = "User ID is required")
        UUID userId
) {
}
