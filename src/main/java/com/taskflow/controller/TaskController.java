package com.taskflow.controller;

import com.taskflow.dto.request.ChangeTaskBoardColumnRequest;
import com.taskflow.dto.request.TaskRequest;
import com.taskflow.dto.response.TaskResponse;
import com.taskflow.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/{boardId}/columns/{columnId}/tasks")
    public ResponseEntity<TaskResponse> createTask(@AuthenticationPrincipal UserDetails user, @PathVariable UUID boardId, @PathVariable UUID columnId, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.createTask(user.getUsername(), boardId, columnId, request));
    }

    @GetMapping("/{boardId}/columns/{columnId}/tasks/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@AuthenticationPrincipal UserDetails user, @PathVariable UUID boardId, @PathVariable UUID columnId, @PathVariable UUID taskId) {
        return ResponseEntity
                .ok(taskService.getTaskById(user.getUsername(), boardId, columnId, taskId));
    }

    @PutMapping("/{boardId}/columns/{columnId}/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@AuthenticationPrincipal UserDetails user, @PathVariable UUID boardId, @PathVariable UUID columnId, @PathVariable UUID taskId, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity
                .ok(taskService.updateTask(user.getUsername(), boardId, columnId, taskId, request));
    }

    @PatchMapping("/{boardId}/columns/{columnId}/tasks/{taskId}")
    public ResponseEntity<TaskResponse> changeTaskBoardColumn(@AuthenticationPrincipal UserDetails user, @PathVariable UUID boardId, @PathVariable UUID columnId, @PathVariable UUID taskId, @Valid @RequestBody ChangeTaskBoardColumnRequest request) {
        return ResponseEntity
                .ok(taskService.changeBoardColumn(user.getUsername(), boardId, columnId, taskId, request));
    }

    @PostMapping("/{boardId}/columns/{columnId}/tasks/{taskId}/assignees/{userId}")
    public ResponseEntity<Void> assignTask(@AuthenticationPrincipal UserDetails user, @PathVariable UUID boardId, @PathVariable UUID columnId, @PathVariable UUID taskId, @PathVariable UUID userId) {
        taskService.assignTask(user.getUsername(), boardId, columnId, taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{boardId}/columns/{columnId}/tasks/{taskId}/assignees/{userId}")
    public ResponseEntity<Void> unassignTask(@AuthenticationPrincipal UserDetails user, @PathVariable UUID boardId, @PathVariable UUID columnId, @PathVariable UUID taskId, @PathVariable UUID userId) {
        taskService.unassignTask(user.getUsername(), boardId, columnId, taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{boardId}/columns/{columnId}/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@AuthenticationPrincipal UserDetails user, @PathVariable UUID boardId, @PathVariable UUID columnId, @PathVariable UUID taskId) {
        taskService.deleteTask(user.getUsername(), boardId, columnId, taskId);
        return ResponseEntity.noContent().build();
    }
}
