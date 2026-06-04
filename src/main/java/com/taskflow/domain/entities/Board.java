package com.taskflow.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "boards")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString(exclude = { "owner", "boardMembers", "boardColumns" })
@EqualsAndHashCode(of = "id")
@Builder
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(length = 255, nullable = false)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(length = 255)
    private String background;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board")
    @Builder.Default
    private List<BoardMember> boardMembers = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board")
    @Builder.Default
    private List<BoardColumn> boardColumns = new ArrayList<>();
}
