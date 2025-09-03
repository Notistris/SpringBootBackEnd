package com.notistris.identityservice.exception;

import com.notistris.identityservice.dto.response.ApiResponse;
import com.notistris.identityservice.enums.ErrorCode;
import com.notistris.identityservice.enums.GlobalErrorCode;
import com.notistris.identityservice.enums.ValidationErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handlingAppException(AppException exception) {
        ApiResponse<ErrorCode> apiResponse = ApiResponse.error(exception.getErrorCode());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handlingValidationException(
            MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        ErrorCode errorCode = ValidationErrorCode.MESSAGE_KEY_INVALID;

        try {
            errorCode = ValidationErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException ignored) {
        }

        ApiResponse<ErrorCode> apiResponse = ApiResponse.error(errorCode);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handlingHttpException(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();
        ErrorCode errorCode = GlobalErrorCode.UNCATEGORIZED_ERROR;

        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            errorCode = ValidationErrorCode.DATE_INVALID;
        } else if (exception.getMessage() != null
                && exception.getMessage().contains("Required request body is missing")) {
            errorCode = GlobalErrorCode.BODY_REQUIRED;
        }

        ApiResponse<ErrorCode> apiResponse = ApiResponse.error(errorCode);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handlingNotFoundPathException() {
        ApiResponse<ErrorCode> apiResponse = ApiResponse.error(GlobalErrorCode.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handlingMethodException() {
        ApiResponse<ErrorCode> apiResponse = ApiResponse.error(GlobalErrorCode.METHOD_NOT_ALLOWED);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(apiResponse);
    }


}
