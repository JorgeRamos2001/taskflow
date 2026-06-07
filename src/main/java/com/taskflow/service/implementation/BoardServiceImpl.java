package com.taskflow.service.implementation;

import com.taskflow.domain.entities.Board;
import com.taskflow.domain.entities.BoardMember;
import com.taskflow.domain.entities.User;
import com.taskflow.domain.enums.BoardMemberRole;
import com.taskflow.domain.ids.BoardMemberId;
import com.taskflow.dto.request.BoardMemberRequest;
import com.taskflow.dto.request.BoardRequest;
import com.taskflow.dto.response.BoardDashboardResponse;
import com.taskflow.dto.response.BoardResponse;
import com.taskflow.dto.response.UserResponse;
import com.taskflow.exception.specific.ConflictException;
import com.taskflow.exception.specific.ResourceNotFoundException;
import com.taskflow.repository.BoardMemberRepository;
import com.taskflow.repository.BoardRepository;
import com.taskflow.repository.UserRepository;
import com.taskflow.service.BoardService;
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
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardMemberRepository boardMemberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BoardDashboardResponse> getMyBoards(String email) {
        log.info("Getting boards for user with email: {}", email);
        User user = getAuthenticatedUser(email);

        List<Board> boards = boardRepository.findByOwnerIdOrMemberUserId(user.getId());
        log.info("Boards found for user: {}", user.getEmail());
        return boards.stream()
                .map(board -> {
                    return new BoardDashboardResponse(
                            board.getId(),
                            board.getTitle(),
                            board.getDescription(),
                            board.getBackground(),
                            board.getCreatedAt(),
                            board.getOwner().getName()
                    );
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponse getBoardById(String email, UUID id) {
        log.info("Getting board with id: {} for user with email: {}", id, email);

        User user = getAuthenticatedUser(email);
        Board board = boardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Board with id " + id + " not found"));

        if (!board.getOwner().getId().equals(user.getId()) && board.getBoardMembers().stream().noneMatch(boardMember -> boardMember.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You are not authorized to access this board");
        }

        log.info("Board found for user: {}", user.getEmail());
        return toBoardResponse(board);
    }

    @Override
    @Transactional
    public BoardResponse createBoard(String email, BoardRequest request) {
        log.info("Creating board for user with email: {}", email);
        User user = getAuthenticatedUser(email);

        Board board = Board.builder()
                .title(request.title())
                .description(request.description())
                .background(request.background())
                .owner(user)
                .build();

        Board savedBoard = boardRepository.save(board);

        BoardMemberId boardMemberId = new BoardMemberId(savedBoard.getId(), user.getId());

        BoardMember boardMember = BoardMember.builder()
                .id(boardMemberId)
                .role(BoardMemberRole.ADMIN)
                .board(savedBoard)
                .user(user)
                .build();

        boardMemberRepository.save(boardMember);

        log.info("Board created successfully for user: {}", user.getEmail());
        return toBoardResponse(savedBoard);
    }

    @Override
    @Transactional
    public BoardResponse updateBoard(String email, UUID id, BoardRequest request) {
        log.info("Updating board with id: {} for user with email: {}", id, email);
        User user = getAuthenticatedUser(email);
        Board board = boardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Board with id " + id + " not found"));

        if (!board.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to access this board");
        }

        board.setTitle(request.title());
        board.setDescription(request.description());
        board.setBackground(request.background());

        Board savedBoard = boardRepository.save(board);

        log.info("Board updated successfully for user: {}", user.getEmail());
        return toBoardResponse(savedBoard);
    }

    @Override
    @Transactional
    public void deleteBoard(String email, UUID id) {
        log.info("Deleting board with id: {} for user with email: {}", id, email);
        User user = getAuthenticatedUser(email);
        Board board = boardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Board with id " + id + " not found"));

        if (!board.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to access this board");
        }

        boardRepository.delete(board);
        log.info("Board deleted successfully for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void addBoardMember(String email, UUID id, BoardMemberRequest request) {
        log.info("Adding member to board with id: {}", id);
        User user = getAuthenticatedUser(email);
        Board board = boardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Board with id " + id + " not found"));

        if (!board.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to access this board");
        }

        User member = userRepository.findById(request.userId()).orElseThrow(() -> new ResourceNotFoundException("User with id " + request.userId() + " not found"));

        if (board.getBoardMembers().stream().anyMatch(boardMember -> boardMember.getUser().getId().equals(member.getId()))) {
            throw new ConflictException("User is already a member of this board");
        }

        BoardMemberId boardMemberId = new BoardMemberId(board.getId(), member.getId());

        BoardMember boardMember = BoardMember.builder()
                .id(boardMemberId)
                .role(request.role())
                .board(board)
                .user(member)
                .build();

        boardMemberRepository.save(boardMember);
        log.info("Member added to board successfully");
    }

    @Override
    @Transactional
    public void removeBoardMember(String email, UUID id, UUID userId) {
        log.info("Removing member from board with id: {}", id);
        User user = getAuthenticatedUser(email);
        Board board = boardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Board with id " + id + " not found"));

        if (!board.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to access this board");
        }

        User member = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        if (board.getOwner().getId().equals(member.getId())) {
            throw new ConflictException("You cannot remove the owner from the board");
        }

        boardMemberRepository.deleteById(new BoardMemberId(board.getId(), member.getId()));
        log.info("Member removed from board successfully");
    }

    private User getAuthenticatedUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
    }

    private BoardResponse toBoardResponse(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getDescription(),
                board.getBackground(),
                board.getUpdatedAt(),
                board.getCreatedAt(),
                new UserResponse(
                        board.getOwner().getId(),
                        board.getOwner().getName(),
                        board.getOwner().getEmail(),
                        board.getOwner().getAvatar()
                ),
                board.getBoardMembers().stream()
                        .map(boardMember -> {
                            User boardUser = boardMember.getUser();
                            return new UserResponse(
                                    boardUser.getId(),
                                    boardUser.getName(),
                                    boardUser.getEmail(),
                                    boardUser.getAvatar()
                            );
                        })
                        .toList()
        );
    }
}
