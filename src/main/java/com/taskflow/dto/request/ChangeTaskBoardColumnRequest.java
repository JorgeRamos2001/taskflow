package com.taskflow.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeTaskBoardColumnRequest(
        @NotNull(message = "New column ID is required")
        UUID newColumnId
) {}
