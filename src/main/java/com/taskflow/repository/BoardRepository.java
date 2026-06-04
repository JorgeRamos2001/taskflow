package com.taskflow.repository;

import com.taskflow.domain.entities.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {
    @Query("SELECT DISTINCT b FROM Board b " +
            "LEFT JOIN b.boardMembers bm " +
            "WHERE b.owner.id = :userId OR bm.user.id = :userId")
    List<Board> findByOwnerIdOrMemberUserId (@Param("userId") UUID userId);
}
