package com.taskflow.service.implementation;

import com.taskflow.domain.entities.Board;
import com.taskflow.domain.entities.BoardColumn;
import com.taskflow.domain.entities.User;
import com.taskflow.dto.request.BoardColumnRequest;
import com.taskflow.dto.request.ChangeColumnPositionRequest;
import com.taskflow.dto.response.BoardColumnResponse;
import com.taskflow.dto.response.TaskSummaryResponse;
import com.taskflow.exception.specific.ConflictException;
import com.taskflow.exception.specific.ResourceNotFoundException;
import com.taskflow.repository.BoardColumnRepository;
import com.taskflow.repository.BoardRepository;
import com.taskflow.repository.UserRepository;
import com.taskflow.service.BoardColumnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardColumnServiceImpl implements BoardColumnService {
    private final BoardColumnRepository boardColumnRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BoardColumnResponse createBoardColumn(String email, UUID boardId, BoardColumnRequest request) {
        log.info("Creating board column for board with id: {}", boardId);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("Board with id " + boardId + " not found"));
        User user = getAuthenticatedUser(email);

        if (!board.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to access this board");
        }

        BoardColumn boardColumn = BoardColumn.builder()
                .name(request.name())
                .position(board.getBoardColumns().size() + 1)
                .board(board)
                .build();

        BoardColumn savedBoardColumn = boardColumnRepository.save(boardColumn);
        log.info("Board column created successfully for board: {}", board.getId());
        return toBoardColumnResponse(savedBoardColumn);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardColumnResponse> getBoardColumns(String email, UUID boardId) {
        log.info("Getting board columns for board with id: {}", boardId);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("Board with id " + boardId + " not found"));
        User user = getAuthenticatedUser(email);

        if (!board.getOwner().getId().equals(user.getId()) && board.getBoardMembers().stream().noneMatch(boardMember -> boardMember.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You are not authorized to access this board");
        }

        List<BoardColumn> columns = boardColumnRepository.findByBoardId(boardId);
        log.info("Board columns found for board: {}", board.getId());
        return columns.stream()
                .map(this::toBoardColumnResponse)
                .toList();
    }

    @Override
    @Transactional
    public BoardColumnResponse updateBoardColumn(String email, UUID boardId, UUID id, BoardColumnRequest request) {
        log.info("Updating board column with id: {}", id);
        BoardColumn boardColumn = boardColumnRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Board column with id " + id + " not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("Board with id " + boardId + " not found"));
        User user = getAuthenticatedUser(email);

        if (!board.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to access this board");
        }

        if (!boardColumn.getBoard().getId().equals(board.getId())) {
            throw new ConflictException("Board column does not belong to this board");
        }

        boardColumn.setName(request.name());
        BoardColumn savedBoardColumn = boardColumnRepository.save(boardColumn);
        log.info("Board column updated successfully for board: {}", board.getId());
        return toBoardColumnResponse(savedBoardColumn);
    }

    @Override
    @Transactional
    public BoardColumnResponse changePosition(String email, UUID boardId, UUID id, ChangeColumnPositionRequest request) {
        log.info("Changing position of board column with id: {}", id);
        BoardColumn boardColumn = boardColumnRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Board column with id " + id + " not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("Board with id " + boardId + " not found"));
        User user = getAuthenticatedUser(email);

        if (!board.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to access this board");
        }

        if (!boardColumn.getBoard().getId().equals(board.getId())) {
            throw new ConflictException("Board column does not belong to this board");
        }

        if (request.position() > board.getBoardColumns().size()) {
            throw new ConflictException("Position cannot be greater than the number of columns in the board");
        }

        if (boardColumn.getPosition().equals(request.position())) {
            return toBoardColumnResponse(boardColumn);
        }

        List<BoardColumn> columns = board.getBoardColumns();

        columns.forEach(column -> {
            if (column.getPosition().equals(boardColumn.getPosition())) {
                return;
            }

            if (boardColumn.getPosition() < request.position()) {
                if (boardColumn.getPosition() < column.getPosition() && column.getPosition() <= request.position()) {
                    column.setPosition(column.getPosition() - 1);
                    boardColumnRepository.save(column);
                }
            }else if (boardColumn.getPosition() > request.position()) {
                if (boardColumn.getPosition() > column.getPosition() && column.getPosition() >= request.position()) {
                    column.setPosition(column.getPosition() + 1);
                    boardColumnRepository.save(column);
                }
            }
        });

        boardColumn.setPosition(request.position());
        BoardColumn savedBoardColumn = boardColumnRepository.save(boardColumn);
        log.info("Board column position changed successfully for board: {}", board.getId());
        return toBoardColumnResponse(savedBoardColumn);
    }

    @Override
    @Transactional
    public void deleteBoardColumn(String email, UUID boardId, UUID id) {
        log.info("Deleting board column with id: {}", id);
        BoardColumn boardColumn = boardColumnRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Board column with id " + id + " not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("Board with id " + boardId + " not found"));
        User user = getAuthenticatedUser(email);

        if (!board.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to access this board");
        }

        if (!boardColumn.getBoard().getId().equals(board.getId())) {
            throw new ConflictException("Board column does not belong to this board");
        }

        boardColumnRepository.delete(boardColumn);
        log.info("Board column deleted successfully for board: {}", board.getId());
    }

    private User getAuthenticatedUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
    }

    private BoardColumnResponse toBoardColumnResponse(BoardColumn boardColumn) {
        return new BoardColumnResponse(
                boardColumn.getId(),
                boardColumn.getBoard().getId(),
                boardColumn.getName(),
                boardColumn.getPosition(),
                boardColumn.getTasks().size(),
                boardColumn.getTasks().stream()
                        .map(task -> {
                            return new TaskSummaryResponse(
                                    task.getId(),
                                    task.getBoardColumn().getId(),
                                    task.getTitle(),
                                    task.getDescription(),
                                    task.getPriority(),
                                    task.getDueDate(),
                                    task.getSubTasks().size(),
                                    task.getComments().size(),
                                    task.getTaskAssignees().size()
                            );
                        }).toList()
        );
    }
}
