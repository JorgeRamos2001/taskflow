package com.taskflow.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SubTaskRequest(
        @NotBlank(message = "Title is required")
        String title
) {
}
