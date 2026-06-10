package com.taskflow.service;

import com.taskflow.dto.request.CommentRequest;
import com.taskflow.dto.request.UpdateCommentRequest;
import com.taskflow.dto.response.CommentResponse;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    CommentResponse createComment(String email, UUID taskId, CommentRequest request);
    List<CommentResponse> getComments(String email, UUID taskId);
    CommentResponse updateComment(String email, UUID taskId, UUID commentId, UpdateCommentRequest request);
    void deleteComment(String email, UUID taskId, UUID commentId);
}
