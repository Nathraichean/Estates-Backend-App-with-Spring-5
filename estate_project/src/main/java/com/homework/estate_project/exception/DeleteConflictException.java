package com.homework.estate_project.exception;

public class DeleteConflictException extends RuntimeException{
    public DeleteConflictException() {
    }

    public DeleteConflictException(String message) {
        super(message);
    }

    public DeleteConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeleteConflictException(Throwable cause) {
        super(cause);
    }
}
