package com.notistris.identityservice.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum GlobalErrorCode implements ErrorCode {

    UNCATEGORIZED_ERROR("ERROR_00", "Uncategorized error"),
    NOT_FOUND("ERROR_01", "Path not found"),
    BODY_REQUIRED("ERROR_02", "Required request body is missing"),
    METHOD_NOT_ALLOWED("ERROR_03", "Method is not allowed"),
    ;

    String code;
    String message;

}
