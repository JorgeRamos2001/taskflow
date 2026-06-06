package com.taskflow.dto.response;

import java.util.UUID;

public record SubTaskResponse(
        UUID id,
        String title,
        Boolean completed
) {
}
