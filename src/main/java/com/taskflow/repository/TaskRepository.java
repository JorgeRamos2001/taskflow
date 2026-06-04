package com.taskflow.repository;

import com.taskflow.domain.entities.Task;
import com.taskflow.domain.enums.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByBoardColumnId(UUID boardColumnId);
}
