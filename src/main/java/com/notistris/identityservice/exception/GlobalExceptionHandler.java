package com.notistris.identityservice.exception;

import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

import jakarta.validation.ConstraintViolation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.nimbusds.jose.JOSEException;
import com.notistris.identityservice.dto.response.ApiResponse;
import com.notistris.identityservice.enums.AuthErrorCode;
import com.notistris.identityservice.enums.ErrorCode;
import com.notistris.identityservice.enums.GlobalErrorCode;
import com.notistris.identityservice.enums.ValidationErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        ApiResponse<ErrorCode> apiResponse = ApiResponse.error(GlobalErrorCode.UNCATEGORIZED_ERROR);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handlingAppException(AppException exception) {
        ApiResponse<ErrorCode> apiResponse = ApiResponse.error(exception.getErrorCode());
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<ErrorCode>> handlingAccessDeniedException() {
        ApiResponse<ErrorCode> apiResponse = ApiResponse.error(AuthErrorCode.FORBIDDEN);
        return ResponseEntity.status(AuthErrorCode.FORBIDDEN.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handlingValidationException(
            MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        Map<String, Object> attributes = null;

        ErrorCode errorCode = ValidationErrorCode.valueOf(enumKey);

        if (errorCode.getMessage().contains("{")) {
            ConstraintViolation<?> constraintViolation =
                    exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();
        }

        ApiResponse<ErrorCode> apiResponse = ApiResponse.error(
                errorCode.getCode(),
                Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception) {
        ErrorCode errorCode = mapToErrorCode(exception);

        if (errorCode == GlobalErrorCode.UNCATEGORIZED_ERROR)
            log.error("Unhandled HttpMessageNotReadableException", exception);

        ApiResponse<ErrorCode> apiResponse = ApiResponse.error(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
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

    @ExceptionHandler({JOSEException.class, ParseException.class})
    public ResponseEntity<ApiResponse<ErrorCode>> handleJoseOrParseException() {
        ApiResponse<ErrorCode> apiResponse = ApiResponse.error(AuthErrorCode.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = attributes.get(MIN_ATTRIBUTE).toString();

        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }

    private ErrorCode mapToErrorCode(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();

        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            return ValidationErrorCode.DATE_INVALID;
        }
        if (cause instanceof com.fasterxml.jackson.core.JsonParseException) {
            return ValidationErrorCode.BODY_INVALID_FORMAT;
        }
        if (exception.getMessage() != null && exception.getMessage().contains("Required request body is missing")) {
            return GlobalErrorCode.BODY_REQUIRED;
        }

        return GlobalErrorCode.UNCATEGORIZED_ERROR;
    }
}
