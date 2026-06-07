package com.taskflow.repository;

import com.taskflow.domain.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    boolean existsByTokenAndUser_Id(String token, UUID userId);
    Integer countAllByUser_Id(UUID userId);
    Optional<RefreshToken> findFirstByUser_IdOrderByCreatedAtAsc(UUID userId);
    void deleteAllByUser_Id(UUID userId);
}
