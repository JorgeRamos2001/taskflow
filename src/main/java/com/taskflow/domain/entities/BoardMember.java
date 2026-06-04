package com.taskflow.domain.entities;

import com.taskflow.domain.enums.BoardMemberRole;
import com.taskflow.domain.ids.BoardMemberId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_members")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString(exclude = { "user", "board" })
@EqualsAndHashCode(of = "id")
@Builder
public class BoardMember {
    @EmbeddedId
    private BoardMemberId id;
    @Enumerated(EnumType.STRING)
    @Column(length = 255, nullable = false)
    private BoardMemberRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("boardId")
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
}
