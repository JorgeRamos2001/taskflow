package com.taskflow.service.implementation;

import com.taskflow.domain.entities.SubTask;
import com.taskflow.domain.entities.Task;
import com.taskflow.domain.entities.User;
import com.taskflow.dto.request.SubTaskRequest;
import com.taskflow.dto.request.UpdateSubTaskRequest;
import com.taskflow.dto.response.SubTaskResponse;
import com.taskflow.exception.specific.ConflictException;
import com.taskflow.exception.specific.ResourceNotFoundException;
import com.taskflow.repository.SubTaskRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import com.taskflow.service.SubTaskService;
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
public class SubTaskServiceImpl implements SubTaskService {
    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SubTaskResponse createSubTask(String email, UUID taskId, SubTaskRequest request) {
        log.info("Creating sub task for task with id: {}", taskId);
        User user = getAuthenticatedUser(email);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));

        if (!task.getBoardColumn().getBoard().getOwner().getId().equals(user.getId()) && task.getBoardColumn().getBoard().getBoardMembers().stream().noneMatch(boardMember -> boardMember.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You are not authorized to access this task");
        }

        SubTask subTask = SubTask.builder()
                .title(request.title())
                .completed(false)
                .task(task)
                .build();

        SubTask savedSubTask = subTaskRepository.save(subTask);
        log.info("Sub task created successfully for task: {}", task.getId());

        return toSubTaskResponse(savedSubTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubTaskResponse> getSubTasks(String email, UUID taskId) {
        log.info("Getting sub tasks for task with id: {}", taskId);
        User user = getAuthenticatedUser(email);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));

        if (!task.getBoardColumn().getBoard().getOwner().getId().equals(user.getId()) && task.getBoardColumn().getBoard().getBoardMembers().stream().noneMatch(boardMember -> boardMember.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You are not authorized to access this task");
        }

        List<SubTask> subTasks = subTaskRepository.findByTaskId(task.getId());
        log.info("Sub tasks found for task: {}", task.getId());
        return subTasks.stream().map(this::toSubTaskResponse).toList();
    }

    @Override
    @Transactional
    public SubTaskResponse updateSubTask(String email, UUID taskId, UUID subTaskId, UpdateSubTaskRequest request) {
        log.info("Updating sub task with id: {}", subTaskId);
        User user = getAuthenticatedUser(email);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));
        SubTask subTask = subTaskRepository.findById(subTaskId).orElseThrow(() -> new ResourceNotFoundException("Sub task with id " + subTaskId + " not found"));
        if (!subTask.getTask().getId().equals(task.getId())) {
            throw new AccessDeniedException("You are not authorized to access this sub task");
        }

        if (!task.getBoardColumn().getBoard().getOwner().getId().equals(user.getId()) && task.getBoardColumn().getBoard().getBoardMembers().stream().noneMatch(boardMember -> boardMember.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You are not authorized to access this task");
        }

        subTask.setTitle(request.title());
        subTask.setCompleted(request.completed());
        SubTask updatedSubTask = subTaskRepository.save(subTask);
        log.info("Sub task updated successfully for task: {}", task.getId());
        return toSubTaskResponse(updatedSubTask);
    }

    @Override
    @Transactional
    public void deleteSubTask(String email, UUID taskId, UUID subTaskId) {
        log.info("Deleting sub task with id: {}", subTaskId);
        User user = getAuthenticatedUser(email);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));
        SubTask subTask = subTaskRepository.findById(subTaskId).orElseThrow(() -> new ResourceNotFoundException("Sub task with id " + subTaskId + " not found"));
        if (!subTask.getTask().getId().equals(task.getId())) {
            throw new AccessDeniedException("You are not authorized to access this sub task");
        }

        if (!task.getBoardColumn().getBoard().getOwner().getId().equals(user.getId()) && task.getBoardColumn().getBoard().getBoardMembers().stream().noneMatch(boardMember -> boardMember.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You are not authorized to access this task");
        }
        subTaskRepository.delete(subTask);
        log.info("Sub task deleted successfully for task: {}", task.getId());
    }

    private User getAuthenticatedUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
    }

    private SubTaskResponse toSubTaskResponse(SubTask subTask) {
        return new SubTaskResponse(
                subTask.getId(),
                subTask.getTitle(),
                subTask.getCompleted()
        );
    }
}
