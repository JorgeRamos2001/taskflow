package com.taskflow.controller;

import com.taskflow.dto.request.SubTaskRequest;
import com.taskflow.dto.request.UpdateSubTaskRequest;
import com.taskflow.dto.response.SubTaskResponse;
import com.taskflow.service.SubTaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Sub Task", description = "Endpoints for sub tasks")
public class SubTaskController {
    private final SubTaskService subTaskService;

    @PostMapping("/{taskId}/subtasks")
    public ResponseEntity<SubTaskResponse> createSubTask(@AuthenticationPrincipal UserDetails user, @PathVariable UUID taskId, @Valid @RequestBody SubTaskRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(subTaskService.createSubTask(user.getUsername(), taskId, request));
    }

    @GetMapping("/{taskId}/subtasks")
    public ResponseEntity<List<SubTaskResponse>> getSubTasks(@AuthenticationPrincipal UserDetails user, @PathVariable UUID taskId) {
        return ResponseEntity
                .ok(subTaskService.getSubTasks(user.getUsername(), taskId));
    }

    @PatchMapping("/{taskId}/subtasks/{subTaskId}")
    public ResponseEntity<SubTaskResponse> updateSubTask(@AuthenticationPrincipal UserDetails user, @PathVariable UUID taskId, @PathVariable UUID subTaskId, @Valid @RequestBody UpdateSubTaskRequest request) {
        return ResponseEntity
                .ok(subTaskService.updateSubTask(user.getUsername(), taskId, subTaskId, request));
    }

    @DeleteMapping("/{taskId}/subtasks/{subTaskId}")
    public ResponseEntity<Void> deleteSubTask(@AuthenticationPrincipal UserDetails user, @PathVariable UUID taskId, @PathVariable UUID subTaskId) {
        subTaskService.deleteSubTask(user.getUsername(), taskId, subTaskId);
        return ResponseEntity.noContent().build();
    }
}
