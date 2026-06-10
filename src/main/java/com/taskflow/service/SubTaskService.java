package com.taskflow.service;

import com.taskflow.dto.request.SubTaskRequest;
import com.taskflow.dto.request.UpdateSubTaskRequest;
import com.taskflow.dto.response.SubTaskResponse;

import java.util.List;
import java.util.UUID;

public interface SubTaskService {
    SubTaskResponse createSubTask(String email, UUID taskId, SubTaskRequest request);
    List<SubTaskResponse> getSubTasks(String email, UUID taskId);
    SubTaskResponse updateSubTask(String email, UUID taskId, UUID subTaskId, UpdateSubTaskRequest request);
    void deleteSubTask(String email, UUID taskId, UUID subTaskId);
}
