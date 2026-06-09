package com.taskflow.service;

import com.taskflow.dto.request.ChangeTaskBoardColumnRequest;
import com.taskflow.dto.request.TaskRequest;
import com.taskflow.dto.response.TaskResponse;

import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(String email, UUID boardId, UUID columnId, TaskRequest request);
    TaskResponse getTaskById(String email, UUID boardId, UUID columnId, UUID taskId);
    TaskResponse updateTask(String email, UUID boardId, UUID columnId, UUID taskId, TaskRequest request);
    TaskResponse changeBoardColumn(String email, UUID boardId, UUID columnId, UUID taskId, ChangeTaskBoardColumnRequest request);
    void assignTask(String email, UUID boardId, UUID columnId, UUID taskId, UUID userId);
    void unassignTask(String email, UUID boardId, UUID columnId, UUID taskId, UUID userId);
    void deleteTask(String email, UUID boardId, UUID columnId, UUID taskId);
}
