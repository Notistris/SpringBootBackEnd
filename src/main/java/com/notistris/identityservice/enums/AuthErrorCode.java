package com.notistris.identityservice.enums;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum AuthErrorCode implements ErrorCode {

    UNAUTHORIZED("AUTH_01", "Unauthorized: Token missing or invalid"),
    FORBIDDEN("AUTH_02", "Forbidden: You don't have permission to access this resource"),
    INCORRECT_CREDENTIALS("AUTH_03", "Incorrect username or password");

    String code;
    String message;

}
