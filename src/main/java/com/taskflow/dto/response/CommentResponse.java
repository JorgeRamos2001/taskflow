package com.taskflow.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID taskId,
        String content,
        LocalDateTime createdAt,
        UserResponse user
) {
}
