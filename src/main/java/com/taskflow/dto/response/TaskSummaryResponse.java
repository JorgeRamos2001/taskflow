package com.taskflow.dto.response;

import com.taskflow.domain.enums.TaskPriority;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskSummaryResponse(
        UUID id,
        UUID columnId,
        String title,
        String description,
        TaskPriority priority,
        LocalDateTime dueDate,
        Integer subTasksCount,
        Integer commentsCount,
        Integer assigneesCount
) {
}
