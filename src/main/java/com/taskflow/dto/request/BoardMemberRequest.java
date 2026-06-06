package com.taskflow.dto.request;

import com.taskflow.domain.enums.BoardMemberRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BoardMemberRequest(
        @NotNull(message = "User ID is required")
        UUID userId,
        @NotNull(message = "Role is required")
        BoardMemberRole role
) {
}
