package com.taskflow.domain.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BoardMemberId implements Serializable {
    @Column(name = "board_id")
    private UUID boardId;
    @Column(name = "user_id")
    private UUID userId;
}
