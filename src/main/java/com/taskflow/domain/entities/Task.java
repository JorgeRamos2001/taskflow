package com.taskflow.domain.entities;

import com.taskflow.domain.enums.TaskPriority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString(exclude = { "boardColumn", "subTasks", "taskAssignees", "comments" })
@EqualsAndHashCode(of = "id")
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(length = 255, nullable = false)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(length = 255, nullable = false)
    private TaskPriority priority;
    @Column(name = "due_date")
    private LocalDateTime dueDate;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id", nullable = false)
    private BoardColumn boardColumn;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task")
    @Builder.Default
    private List<SubTask> subTasks = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task")
    @Builder.Default
    private List<TaskAssignee> taskAssignees = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}
