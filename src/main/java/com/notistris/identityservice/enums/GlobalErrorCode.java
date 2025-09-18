package com.notistris.identityservice.enums;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum GlobalErrorCode implements ErrorCode {
    UNCATEGORIZED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_00", "Uncategorized error"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "ERROR_01", "Path not found"),
    BODY_REQUIRED(HttpStatus.BAD_REQUEST, "ERROR_02", "Required request body is missing"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "ERROR_03", "Method is not allowed"),
    ;

    HttpStatus httpStatus;
    String code;
    String message;
}
