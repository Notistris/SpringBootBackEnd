package com.notistris.identityservice.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND("USER_01", "User not found"),
    USER_ALREADY_EXISTS("USER_02", "User already exists");

    String code;
    String message;

}
