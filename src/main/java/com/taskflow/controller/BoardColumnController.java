package com.taskflow.controller;

import com.taskflow.dto.request.BoardColumnRequest;
import com.taskflow.dto.request.ChangeColumnPositionRequest;
import com.taskflow.dto.response.BoardColumnResponse;
import com.taskflow.service.BoardColumnService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Board Column", description = "Endpoints for board columns")
public class BoardColumnController {
    private final BoardColumnService boardColumnService;

    @PostMapping("/{boardId}/columns")
    public ResponseEntity<BoardColumnResponse> createBoardColumn(@AuthenticationPrincipal UserDetails user, @PathVariable UUID boardId, @Valid @RequestBody BoardColumnRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(boardColumnService.createBoardColumn(user.getUsername(), boardId, request));
    }

    @GetMapping("/{boardId}/columns")
    public ResponseEntity<List<BoardColumnResponse>> getBoardColumns(@AuthenticationPrincipal UserDetails user, @PathVariable UUID boardId) {
        return ResponseEntity
                .ok(boardColumnService.getBoardColumns(user.getUsername(), boardId));
    }

    @PutMapping("/{boardId}/columns/{columnId}")
    public ResponseEntity<BoardColumnResponse> updateBoardColumn(@AuthenticationPrincipal UserDetails user, @PathVariable UUID boardId, @PathVariable UUID columnId, @Valid @RequestBody BoardColumnRequest request) {
        return ResponseEntity
                .ok(boardColumnService.updateBoardColumn(user.getUsername(), boardId, columnId, request));
    }

    @PatchMapping("/{boardId}/columns/{columnId}/position")
    public ResponseEntity<BoardColumnResponse> changeBoardColumnPosition(@AuthenticationPrincipal UserDetails user, @PathVariable UUID boardId, @PathVariable UUID columnId, @Valid @RequestBody ChangeColumnPositionRequest request) {
        return ResponseEntity
                .ok(boardColumnService.changePosition(user.getUsername(), boardId, columnId, request));
    }

    @DeleteMapping("/{boardId}/columns/{columnId}")
    public ResponseEntity<Void> deleteBoardColumn(@AuthenticationPrincipal UserDetails user, @PathVariable UUID boardId, @PathVariable UUID columnId) {
        boardColumnService.deleteBoardColumn(user.getUsername(), boardId, columnId);
        return ResponseEntity.noContent().build();
    }
}
