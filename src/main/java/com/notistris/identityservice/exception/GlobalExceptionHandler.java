package com.notistris.identityservice.exception;


import com.notistris.identityservice.dto.request.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final String ValidationErrorCode = "VALIDATION_01";

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handlingRuntimeException(AppException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.error(exception.getErrorCode().getCode(), exception.getErrorCode().getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handlingValidationException(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ValidationErrorCode, Objects.requireNonNull(exception.getFieldError()).getDefaultMessage()));
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handlingDateTimeException(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ValidationErrorCode, "Invalid date format"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error("ERROR_01", exception.getMessage()));

    }
}
