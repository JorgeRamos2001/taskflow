package com.taskflow.service;

import com.taskflow.dto.request.BoardMemberRequest;
import com.taskflow.dto.request.BoardRequest;
import com.taskflow.dto.response.BoardDashboardResponse;
import com.taskflow.dto.response.BoardResponse;

import java.util.List;
import java.util.UUID;

public interface BoardService {
    List<BoardDashboardResponse> getMyBoards(String email);
    BoardResponse getBoardById(String email, UUID id);
    BoardResponse createBoard(String email, BoardRequest request);
    BoardResponse updateBoard(String email,UUID id, BoardRequest request);
    void deleteBoard(String email, UUID id);

    void addBoardMember(String email, UUID id, BoardMemberRequest request);
    void removeBoardMember(String email, UUID id, UUID userId);
}
