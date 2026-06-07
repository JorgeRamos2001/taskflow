package com.taskflow.service.implementation;

import com.taskflow.domain.entities.User;
import com.taskflow.domain.enums.UserState;
import com.taskflow.dto.request.ChangePasswordRequest;
import com.taskflow.dto.request.UpdateUserRequest;
import com.taskflow.dto.response.UserResponse;
import com.taskflow.exception.specific.ConflictException;
import com.taskflow.exception.specific.ResourceNotFoundException;
import com.taskflow.repository.RefreshTokenRepository;
import com.taskflow.repository.UserRepository;
import com.taskflow.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyUser(String email) {
        log.info("Getting user with email: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
        log.info("User found: {}", user.getEmail());
        return toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateMyUser(String email, UpdateUserRequest request) {
        log.info("Updating user with email: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));

        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }

        user.setName(request.name());
        user.setEmail(request.email());

        if (request.avatar() != null) {
            user.setAvatar(request.avatar());
        }

        User savedUser = userRepository.save(user);

        log.info("User updated successfully: {}", savedUser.getEmail());

        return toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse changePassword(String email, ChangePasswordRequest request) {
        log.info("Changing password for user with email: {}", email);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        User savedUser = userRepository.save(user);

        log.info("Password changed successfully for user: {}", savedUser.getEmail());

        return toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public void deleteMyUser(String email) {
        log.info("Deleting user with email: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
        user.setState(UserState.DELETED);

        userRepository.save(user);
        refreshTokenRepository.deleteAllByUser_Id(user.getId());

        log.info("User deleted successfully: {}", user.getEmail());
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
