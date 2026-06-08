package com.taskflow.service;

import com.taskflow.dto.request.BoardColumnRequest;
import com.taskflow.dto.request.ChangeColumnPositionRequest;
import com.taskflow.dto.response.BoardColumnResponse;

import java.util.List;
import java.util.UUID;

public interface BoardColumnService {
    BoardColumnResponse createBoardColumn(String email, UUID boardId, BoardColumnRequest request);
    List<BoardColumnResponse> getBoardColumns(String email, UUID boardId);
    BoardColumnResponse updateBoardColumn(String email, UUID boardId, UUID id, BoardColumnRequest request);
    BoardColumnResponse changePosition(String email, UUID boardId, UUID id, ChangeColumnPositionRequest request);
    void deleteBoardColumn(String email, UUID boardId, UUID id);
}
