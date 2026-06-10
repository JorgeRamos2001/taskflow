package com.taskflow.controller;

import com.taskflow.dto.request.CommentRequest;
import com.taskflow.dto.request.UpdateCommentRequest;
import com.taskflow.dto.response.CommentResponse;
import com.taskflow.service.CommentService;
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
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{taskId}/comments")
    public ResponseEntity<CommentResponse> createComment(@AuthenticationPrincipal UserDetails user, @PathVariable UUID taskId, @Valid @RequestBody CommentRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.createComment(user.getUsername(), taskId, request));
    }

    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<CommentResponse>> getComment(@AuthenticationPrincipal UserDetails user, @PathVariable UUID taskId) {
        return ResponseEntity
                .ok(commentService.getComments(user.getUsername(), taskId));
    }

    @PatchMapping("/{taskId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@AuthenticationPrincipal UserDetails user, @PathVariable UUID taskId, @PathVariable UUID commentId, @Valid @RequestBody UpdateCommentRequest request) {
        return ResponseEntity
                .ok(commentService.updateComment(user.getUsername(), taskId, commentId, request));
    }

    @DeleteMapping("/{taskId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal UserDetails user, @PathVariable UUID taskId, @PathVariable UUID commentId) {
        commentService.deleteComment(user.getUsername(), taskId, commentId);
        return ResponseEntity.noContent().build();
    }
}
