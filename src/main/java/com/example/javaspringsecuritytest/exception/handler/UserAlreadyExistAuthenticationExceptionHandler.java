package com.example.javaspringsecuritytest.exception.handler;

import com.example.javaspringsecuritytest.exception.UserAlreadyExistAuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserAlreadyExistAuthenticationExceptionHandler {

    @ExceptionHandler({UserAlreadyExistAuthenticationException.class})
    public ResponseEntity<String> handleException(UserAlreadyExistAuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
    }
}
