package com.notistris.identityservice.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum UserErrorCode implements ErrorCode {

    USER_NOT_EXISTS(HttpStatus.BAD_REQUEST, "USER_01", "User not exists"),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_02", "User already exists");

    HttpStatus httpStatus;
    String code;
    String message;

}
