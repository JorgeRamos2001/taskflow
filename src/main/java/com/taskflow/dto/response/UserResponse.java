package com.taskflow.dto.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String avatar
) {
}
