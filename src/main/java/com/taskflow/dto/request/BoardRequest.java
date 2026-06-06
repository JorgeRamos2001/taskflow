package com.taskflow.dto.request;

import jakarta.validation.constraints.NotBlank;

public record BoardRequest(
        @NotBlank(message = "Title is required")
        String title,
        String description,
        @NotBlank(message = "Background is required")
        String background
) {
}
