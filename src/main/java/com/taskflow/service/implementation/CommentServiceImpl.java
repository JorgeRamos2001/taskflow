package com.taskflow.service.implementation;

import com.taskflow.domain.entities.Comment;
import com.taskflow.domain.entities.Task;
import com.taskflow.domain.entities.User;
import com.taskflow.dto.request.CommentRequest;
import com.taskflow.dto.request.UpdateCommentRequest;
import com.taskflow.dto.response.CommentResponse;
import com.taskflow.dto.response.UserResponse;
import com.taskflow.exception.specific.ResourceNotFoundException;
import com.taskflow.repository.CommentRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import com.taskflow.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentResponse createComment(String email, UUID taskId, CommentRequest request) {
        log.info("Creating comment for task with id: {}", taskId);
        User user = getAuthenticatedUser(email);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));

        if (!task.getBoardColumn().getBoard().getOwner().getId().equals(user.getId()) && task.getBoardColumn().getBoard().getBoardMembers().stream().noneMatch(boardMember -> boardMember.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You are not authorized to access this task");
        }

        Comment comment = Comment.builder()
                .content(request.content())
                .task(task)
                .user(user)
                .build();
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created successfully for task: {}", task.getId());
        return toCommentResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(String email, UUID taskId) {
        log.info("Getting comments for task with id: {}", taskId);
        User user = getAuthenticatedUser(email);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));

        if (!task.getBoardColumn().getBoard().getOwner().getId().equals(user.getId()) && task.getBoardColumn().getBoard().getBoardMembers().stream().noneMatch(boardMember -> boardMember.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You are not authorized to access this task");
        }
        List<Comment> comments = commentRepository.findByTaskId(taskId);
        log.info("Comments found for task: {}", task.getId());
        return comments.stream().map(this::toCommentResponse).toList();
    }

    @Override
    @Transactional
    public CommentResponse updateComment(String email, UUID taskId, UUID commentId, UpdateCommentRequest request) {
        log.info("Updating comment with id: {}", commentId);
        User user = getAuthenticatedUser(email);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment with id " + commentId + " not found"));

        if (!task.getBoardColumn().getBoard().getOwner().getId().equals(user.getId()) && task.getBoardColumn().getBoard().getBoardMembers().stream().noneMatch(boardMember -> boardMember.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You are not authorized to access this task");
        }

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to update this comment");
        }

        if (!comment.getTask().getId().equals(taskId)) {
            throw new AccessDeniedException("You are not authorized to update this comment");
        }

        comment.setContent(request.content());
        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment updated successfully for task: {}", task.getId());
        return toCommentResponse(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(String email, UUID taskId, UUID commentId) {
        log.info("Deleting comment with id: {}", commentId);
        User user = getAuthenticatedUser(email);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment with id " + commentId + " not found"));

        if (!task.getBoardColumn().getBoard().getOwner().getId().equals(user.getId()) && task.getBoardColumn().getBoard().getBoardMembers().stream().noneMatch(boardMember -> boardMember.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You are not authorized to access this task");
        }

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this comment");
        }

        if (!comment.getTask().getId().equals(taskId)) {
            throw new AccessDeniedException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
        log.info("Comment deleted successfully for task: {}", task.getId());
    }

    private User getAuthenticatedUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
    }

    private CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getTask().getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                new UserResponse(
                        comment.getUser().getId(),
                        comment.getUser().getName(),
                        comment.getUser().getEmail(),
                        comment.getUser().getAvatar()
                )
        );
    }
}
