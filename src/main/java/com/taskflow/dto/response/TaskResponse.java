package com.taskflow.dto.response;

import com.taskflow.domain.enums.TaskPriority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID columnId,
        String title,
        String description,
        TaskPriority priority,
        LocalDateTime dueDate,
        List<SubTaskResponse> subTasks,
        List<CommentResponse> comments,
        List<UserResponse> assignees
) {
}
