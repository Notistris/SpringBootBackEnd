package com.notistris.identityservice.exception;

public enum GlobalErrorCode implements ErrorCode {
    UNCATEGORIZED_ERROR("ERROR_00", "Uncategorized error"),
    NOT_FOUND("ERROR_01", "Path not found"),
    BODY_REQUIRED("ERROR_02", "Required request body is missing"),
    METHOD_NOT_ALLOWED("ERROR_03", "Method is not allowed"),
    ;

    private final String code;
    private final String message;

    GlobalErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
