package com.notistris.identityservice.exception;


public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_01", "User not found"),
    USER_ALREADY_EXISTS("USER_02", "User already exists");


    private final String code;
    private final String message;

    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

