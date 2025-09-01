package com.notistris.identityservice.exception;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum AuthErrorCode implements ErrorCode {

    INCORRECT_CREDENTIALS("AUTH_01", "Incorrect username or password");

    String code;
    String message;
}
