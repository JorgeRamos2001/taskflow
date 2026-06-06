package com.taskflow.dto.request;

import com.taskflow.domain.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskRequest(
        @NotNull(message = "Column ID is required")
        UUID columnId,
        @NotBlank(message = "Title is required")
        String title,
        String description,
        @NotNull(message = "Priority is required")
        TaskPriority priority,
        LocalDateTime dueDate
) {
}
