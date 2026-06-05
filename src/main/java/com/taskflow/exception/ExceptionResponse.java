package com.taskflow.exception;

import java.time.LocalDateTime;

public record ExceptionResponse(
        Integer code,
        String message,
        Object details,
        String path,
        LocalDateTime timestamp
) {
}
