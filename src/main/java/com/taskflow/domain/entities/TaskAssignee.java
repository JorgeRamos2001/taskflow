package com.taskflow.domain.entities;

import com.taskflow.domain.ids.TaskAssigneeId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_assignees")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString(exclude = { "user", "task" })
@EqualsAndHashCode(of = "id")
@Builder
public class TaskAssignee {
    @EmbeddedId
    private TaskAssigneeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
}
