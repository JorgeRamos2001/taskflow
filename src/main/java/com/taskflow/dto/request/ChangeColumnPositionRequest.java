package com.taskflow.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ChangeColumnPositionRequest(
        @NotNull(message = "Column position is required")
        @Min(value = 1, message = "Column position must be greater than or equal to 1")
        Integer position
) {
}
