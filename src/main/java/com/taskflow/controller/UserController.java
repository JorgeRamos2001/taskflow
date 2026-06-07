package com.taskflow.controller;

import com.taskflow.domain.entities.User;
import com.taskflow.dto.request.ChangePasswordRequest;
import com.taskflow.dto.request.UpdateUserRequest;
import com.taskflow.dto.response.UserResponse;
import com.taskflow.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyUser(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(userService.getMyUser(user.getUsername()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyUser(@AuthenticationPrincipal UserDetails user, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateMyUser(user.getUsername(), request));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<UserResponse> changePassword(@AuthenticationPrincipal UserDetails user,@Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(userService.changePassword(user.getUsername(), request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyUser(@AuthenticationPrincipal UserDetails user) {
        userService.deleteMyUser(user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
