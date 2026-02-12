package com.example.exception;

import com.example.dto.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    // 404 Пользователь не найден
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
        Long userId = extractId(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                "Пользователь не найден: " + userId,
                "/api/users/" + userId,
                404
            ));
    }
    
    // 408/503 DummyJSON timeout
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleExternalError(ResourceAccessException e) {
        String msg = "External API ошибка";
        int status = 503;
        
        if (e.getCause() instanceof SocketTimeoutException) {
            msg = "DummyJSON timeout — попробуйте позже";
            status = 408;
        }
        
        return ResponseEntity.status(status)
            .body(new ErrorResponse(msg, e.getMessage(), status));
    }
    
    // 400 Неверные параметры
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(e.getMessage(), null, 400));
    }
    
    // 500 Любые другие ошибки
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
        log.error("Неожиданная ошибка", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("Внутренняя ошибка сервера", e.getMessage(), 500));
    }
    
    private Long extractId(String message) {
        try {
            return message.contains(": ") ? 
                Long.parseLong(message.split(": ")[1]) : -1L;
        } catch (Exception ex) {
            return -1L;
        }
    }
}
