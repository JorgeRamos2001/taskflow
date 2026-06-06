package com.taskflow.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BoardResponse(
        UUID id,
        String title,
        String description,
        String background,
        LocalDateTime updatedAt,
        LocalDateTime createdAt,
        UserResponse owner,
        List<UserResponse> members
) {
}
