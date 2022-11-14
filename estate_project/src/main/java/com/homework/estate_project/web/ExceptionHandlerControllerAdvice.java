package com.homework.estate_project.web;

import com.homework.estate_project.dto.ErrorResponse;
import com.homework.estate_project.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNonexistingEntityException(NonExistingEntityException ex) {
        return ResponseEntity.status(NOT_FOUND).body(new ErrorResponse(NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInvalidEntityDataException(InvalidEntityDataException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(BAD_REQUEST.value(), ex.getMessage(), ex.getConstraintViolations()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnautorizedException(UnautorizedException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInsufficientPrivilegiesException(InsufficientPrivilegiesException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDeleteConflictException(DeleteConflictException ex){
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
}
