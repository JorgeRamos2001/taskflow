package com.taskflow.repository;

import com.taskflow.domain.entities.BoardMember;
import com.taskflow.domain.ids.BoardMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMember, BoardMemberId> {
}
