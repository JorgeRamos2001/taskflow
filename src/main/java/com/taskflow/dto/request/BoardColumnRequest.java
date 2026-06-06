package com.taskflow.dto.request;

import jakarta.validation.constraints.NotBlank;

public record BoardColumnRequest(
        @NotBlank(message = "Name is required")
        String name
) {
}
