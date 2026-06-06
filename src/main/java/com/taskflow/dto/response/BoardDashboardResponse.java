package com.taskflow.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record BoardDashboardResponse(
        UUID id,
        String title,
        String description,
        String background,
        LocalDateTime createdAt,
        String owner
) {
}
