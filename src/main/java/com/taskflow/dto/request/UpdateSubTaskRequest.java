package com.taskflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSubTaskRequest(
        @NotBlank(message = "Title is required")
        String title,
        @NotNull(message = "Completed is required")
        Boolean completed
) {
}
