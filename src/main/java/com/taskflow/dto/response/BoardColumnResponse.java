package com.taskflow.dto.response;

import java.util.List;
import java.util.UUID;

public record BoardColumnResponse(
        UUID id,
        UUID boardId,
        String name,
        Integer position,
        Integer tasksCount,
        List<TaskResponse> tasks
) {
}
