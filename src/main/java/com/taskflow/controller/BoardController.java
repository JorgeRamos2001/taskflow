package com.taskflow.controller;

import com.taskflow.dto.request.BoardMemberRequest;
import com.taskflow.dto.request.BoardRequest;
import com.taskflow.dto.response.BoardDashboardResponse;
import com.taskflow.dto.response.BoardResponse;
import com.taskflow.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<List<BoardDashboardResponse>> getAllBoards(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(boardService.getMyBoards(user.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoardById(@AuthenticationPrincipal UserDetails user, @PathVariable UUID id) {
        return ResponseEntity.ok(boardService.getBoardById(user.getUsername(), id));
    }

    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@AuthenticationPrincipal UserDetails user, @Valid @RequestBody BoardRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(boardService.createBoard(user.getUsername(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse> updateBoard(@AuthenticationPrincipal UserDetails user, @PathVariable UUID id, @Valid @RequestBody BoardRequest request) {
        return ResponseEntity.ok(boardService.updateBoard(user.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@AuthenticationPrincipal UserDetails user, @PathVariable UUID id) {
        boardService.deleteBoard(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<Void> addBoardMember(@AuthenticationPrincipal UserDetails user, @PathVariable UUID id, @Valid @RequestBody BoardMemberRequest request) {
        boardService.addBoardMember(user.getUsername(), id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeBoardMember(@AuthenticationPrincipal UserDetails user, @PathVariable UUID id, @PathVariable UUID userId) {
        boardService.removeBoardMember(user.getUsername(), id, userId);
        return ResponseEntity.noContent().build();
    }
}
