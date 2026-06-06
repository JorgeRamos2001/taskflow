package com.taskflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CommentRequest(
        @NotNull(message = "Task ID is required")
        UUID taskId,
        @NotBlank(message = "Content is required")
        String content
) {
}
