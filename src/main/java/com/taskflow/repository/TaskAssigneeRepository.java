package com.taskflow.repository;

import com.taskflow.domain.entities.TaskAssignee;
import com.taskflow.domain.ids.TaskAssigneeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskAssigneeRepository extends JpaRepository<TaskAssignee, TaskAssigneeId> {
}
