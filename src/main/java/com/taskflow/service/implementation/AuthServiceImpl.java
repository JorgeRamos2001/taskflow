package com.taskflow.service.implementation;

import com.taskflow.domain.entities.RefreshToken;
import com.taskflow.domain.entities.User;
import com.taskflow.domain.enums.UserState;
import com.taskflow.dto.request.LoginRequest;
import com.taskflow.dto.request.LogoutRequest;
import com.taskflow.dto.request.RefreshTokenRequest;
import com.taskflow.dto.request.UserRequest;
import com.taskflow.dto.response.AuthResponse;
import com.taskflow.dto.response.UserResponse;
import com.taskflow.exception.specific.ConflictException;
import com.taskflow.exception.specific.RefreshTokenExpiredException;
import com.taskflow.exception.specific.ResourceNotFoundException;
import com.taskflow.repository.RefreshTokenRepository;
import com.taskflow.repository.UserRepository;
import com.taskflow.security.JwtService;
import com.taskflow.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Value( "${app.jwt.refresh-expiration-ms}")
    private Long REFRESH_TOKEN_EXPIRATION_TIME;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Login request received for user: {}", loginRequest.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );

        User user = (User) authentication.getPrincipal();

        if (refreshTokenRepository.countAllByUser_Id(user.getId()) >= 5) {
            refreshTokenRepository.delete(refreshTokenRepository.findFirstByUser_IdOrderByCreatedAtAsc(user.getId()).get());
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = generateAndSaveRefreshToken(user);

        log.info("Login successful for user: {}", loginRequest.email());
        return new AuthResponse(
                toUserResponse(user),
                accessToken,
                refreshToken
        );
    }

    @Override
    @Transactional
    public AuthResponse register(UserRequest userRequest) {
        log.info("Register request received for use: {}", userRequest.email());

        if (userRepository.existsByEmail(userRequest.email())) {
            throw new ConflictException("Email already exists");
        }

        User user = User.builder()
                .name(userRequest.name())
                .email(userRequest.email())
                .password(passwordEncoder.encode(userRequest.password()))
                .avatar(null)
                .state(UserState.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = generateAndSaveRefreshToken(savedUser);

        log.info("User registered successfully: {}", savedUser.getEmail());

        return new AuthResponse(
                toUserResponse(savedUser),
                accessToken,
                refreshToken
        );
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        log.info("Refresh token request received for token: {}", refreshTokenRequest.refreshToken());

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenRequest.refreshToken()).orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenExpiredException("Refresh token expired");
        }

        User user = refreshToken.getUser();

        String accessToken = jwtService.generateAccessToken(user);

        log.info("Refresh token successful for user: {}", user.getEmail());

        return new AuthResponse(
                toUserResponse(user),
                accessToken,
                refreshTokenRequest.refreshToken()
        );
    }

    @Override
    @Transactional
    public void logout(LogoutRequest logoutRequest) {
        log.info("Logout request received");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(logoutRequest.refreshToken()).orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        refreshTokenRepository.delete(refreshToken);

        log.info("Logout successful");
    }

    private String generateAndSaveRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(jwtService.generateRefreshToken(user))
                .expiresAt(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION_TIME / 1000))
                .user(user)
                .build();

        return refreshTokenRepository.save(refreshToken).getToken();
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAvatar()
        );
    }
}
