package com.taskflow.exception.specific;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
