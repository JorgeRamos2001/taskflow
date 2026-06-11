package com.taskflow.service.implementation;

import com.taskflow.domain.entities.*;
import com.taskflow.domain.ids.TaskAssigneeId;
import com.taskflow.dto.request.ChangeTaskBoardColumnRequest;
import com.taskflow.dto.request.TaskRequest;
import com.taskflow.dto.response.CommentResponse;
import com.taskflow.dto.response.SubTaskResponse;
import com.taskflow.dto.response.TaskResponse;
import com.taskflow.dto.response.UserResponse;
import com.taskflow.exception.specific.ConflictException;
import com.taskflow.exception.specific.ResourceNotFoundException;
import com.taskflow.repository.*;
import com.taskflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final UserRepository userRepository;
    private final TaskAssigneeRepository taskAssigneeRepository;

    @Override
    @Transactional
    public TaskResponse createTask(String email, UUID boardId, UUID columnId, TaskRequest request) {
        log.info("Creating task for board with id: {}", boardId);
        User user = getAuthenticatedUser(email);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("Board with id " + boardId + " not found"));
        BoardColumn boardColumn = boardColumnRepository.findById(columnId).orElseThrow(() -> new ResourceNotFoundException("Board column with id " + columnId + " not found"));

        validateBoardColumn(user, board, boardColumn);

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .priority(request.priority())
                .dueDate(request.dueDate())
                .boardColumn(boardColumn)
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully for board: {}", board.getId());
        return toTaskResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(String email, UUID boardId, UUID columnId, UUID taskId) {
        log.info("Getting task with id: {}", taskId);
        User user = getAuthenticatedUser(email);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("Board with id " + boardId + " not found"));
        BoardColumn boardColumn = boardColumnRepository.findById(columnId).orElseThrow(() -> new ResourceNotFoundException("Board column with id " + columnId + " not found"));

        validateBoardColumn(user, board, boardColumn);

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));

        if (!task.getBoardColumn().getId().equals(boardColumn.getId())) {
            throw new AccessDeniedException("You are not authorized to access this task");
        }

        log.info("Task found for board: {}", board.getId());
        return toTaskResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(String email, UUID boardId, UUID columnId, UUID taskId, TaskRequest request) {
        log.info("Updating task with id: {}", taskId);
        User user = getAuthenticatedUser(email);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("Board with id " + boardId + " not found"));
        BoardColumn boardColumn = boardColumnRepository.findById(columnId).orElseThrow(() -> new ResourceNotFoundException("Board column with id " + columnId + " not found"));

        validateBoardColumn(user, board, boardColumn);

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));

        if (!task.getBoardColumn().getId().equals(boardColumn.getId())) {
            throw new AccessDeniedException("You are not authorized to access this task");
        }

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully for board: {}", board.getId());
        return toTaskResponse(updatedTask);
    }

    @Override
    @Transactional
    public TaskResponse changeBoardColumn(String email, UUID boardId, UUID columnId, UUID taskId, ChangeTaskBoardColumnRequest request) {
        log.info("Changing board column of task with id: {}", taskId);
        User user = getAuthenticatedUser(email);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("Board with id " + boardId + " not found"));
        BoardColumn boardColumn = boardColumnRepository.findById(columnId).orElseThrow(() -> new ResourceNotFoundException("Board column with id " + columnId + " not found"));
        BoardColumn newBoardColumn = boardColumnRepository.findById(request.newColumnId()).orElseThrow(() -> new ResourceNotFoundException("New board column with id " + request.newColumnId() + " not found"));

        validateBoardColumn(user, board, boardColumn);
        validateBoardColumn(user, board, newBoardColumn);

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));

        task.setBoardColumn(newBoardColumn);
        Task updatedTask = taskRepository.save(task);
        log.info("Board column of task changed successfully for board: {}", board.getId());
        return toTaskResponse(updatedTask);
    }

    @Override
    @Transactional
    public void assignTask(String email, UUID boardId, UUID columnId, UUID taskId, UUID userId) {
        log.info("Assigning task with id: {} to user with id: {}", taskId, userId);
        User user = getAuthenticatedUser(email);
        User assignee = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("Board with id " + boardId + " not found"));
        BoardColumn boardColumn = boardColumnRepository.findById(columnId).orElseThrow(() -> new ResourceNotFoundException("Board column with id " + columnId + " not found"));
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));

        if (!board.getOwner().getId().equals(user.getId()) && board.getBoardMembers().stream().noneMatch(boardMember -> boardMember.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You are not authorized to access this board");
        }

        if (!task.getBoardColumn().getId().equals(boardColumn.getId())) {
            throw new ConflictException("Task does not belong to this board column");
        }

        if (task.getTaskAssignees().stream().anyMatch(taskAssignee -> taskAssignee.getUser().getId().equals(assignee.getId()))) {
            throw new ConflictException("User is already assigned to this task");
        }

        TaskAssigneeId taskAssigneeId = new TaskAssigneeId(task.getId(), assignee.getId());
        TaskAssignee taskAssignee = TaskAssignee.builder()
                .id(taskAssigneeId)
                .user(assignee)
                .task(task)
                .build();
        //task.getTaskAssignees().add(taskAssignee);
        //taskRepository.save(task);
        taskAssigneeRepository.save(taskAssignee);
        log.info("Task assigned successfully for board: {}", board.getId());
    }

    @Override
    @Transactional
    public void unassignTask(String email, UUID boardId, UUID columnId, UUID taskId, UUID userId) {
        log.info("Unassigning task with id: {} from user with id: {}", taskId, userId);
        User user = getAuthenticatedUser(email);
        User assignee = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("Board with id " + boardId + " not found"));
        BoardColumn boardColumn = boardColumnRepository.findById(columnId).orElseThrow(() -> new ResourceNotFoundException("Board column with id " + columnId + " not found"));
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));

        if (!board.getOwner().getId().equals(user.getId()) && board.getBoardMembers().stream().noneMatch(boardMember -> boardMember.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You are not authorized to access this board");
        }

        if (!task.getBoardColumn().getId().equals(boardColumn.getId())) {
            throw new ConflictException("Task does not belong to this board column");
        }

        if (task.getTaskAssignees().stream().noneMatch(taskAssignee -> taskAssignee.getUser().getId().equals(assignee.getId()))) {
            throw new ConflictException("User is not assigned to this task");
        }

        task.getTaskAssignees().removeIf(taskAssignee -> taskAssignee.getUser().getId().equals(assignee.getId()));
        taskRepository.save(task);
        log.info("Task unassigned successfully for board: {}", board.getId());
    }

    @Override
    @Transactional
    public void deleteTask(String email, UUID boardId, UUID columnId, UUID taskId) {
        log.info("Deleting task with id: {}", taskId);
        User user = getAuthenticatedUser(email);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("Board with id " + boardId + " not found"));
        BoardColumn boardColumn = boardColumnRepository.findById(columnId).orElseThrow(() -> new ResourceNotFoundException("Board column with id " + columnId + " not found"));

        validateBoardColumn(user, board, boardColumn);

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));
        taskRepository.delete(task);
        log.info("Task deleted successfully for board: {}", board.getId());
    }

    private User getAuthenticatedUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
    }

    private void validateBoardColumn(User user, Board board, BoardColumn boardColumn) {
        if (!board.getOwner().getId().equals(user.getId()) && board.getBoardMembers().stream().noneMatch(boardMember -> boardMember.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You are not authorized to access this board");
        }

        if (!boardColumn.getBoard().getId().equals(board.getId())) {
            throw new AccessDeniedException("Board column does not belong to this board");
        }
    }

    private TaskResponse toTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getBoardColumn().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getDueDate(),
                task.getSubTasks().stream()
                        .map(subTask -> {
                            return new SubTaskResponse(
                                    subTask.getId(),
                                    subTask.getTitle(),
                                    subTask.getCompleted()
                            );
                        })
                        .toList(),
                task.getComments().stream()
                        .map(comment -> {
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
                        })
                        .toList(),
                task.getTaskAssignees().stream()
                        .map(taskAssignee -> {
                            return new UserResponse(
                                    taskAssignee.getUser().getId(),
                                    taskAssignee.getUser().getName(),
                                    taskAssignee.getUser().getEmail(),
                                    taskAssignee.getUser().getAvatar()
                            );
                        })
                        .toList()
        );
    }
}
