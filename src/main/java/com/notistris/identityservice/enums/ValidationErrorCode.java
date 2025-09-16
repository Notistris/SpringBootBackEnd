package com.notistris.identityservice.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ValidationErrorCode implements ErrorCode {

    MESSAGE_KEY_INVALID(HttpStatus.BAD_REQUEST, "INVALID_00", "Invalid message key"),
    FIELD_BLANK(HttpStatus.BAD_REQUEST, "INVALID_01", "Request body missing fields"),
    BODY_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_02", "Invalid body format"),
    USER_INVALID(HttpStatus.BAD_REQUEST, "INVALID_03", "Username must be at least {min} characters"),
    PASSWORD_INVALID(HttpStatus.BAD_REQUEST, "INVALID_04", "Password must be at least {min} characters"),
    DOB_INVALID(HttpStatus.BAD_REQUEST, "INVALID_05", "Your age must be at least {min}"),
    DATE_INVALID(HttpStatus.BAD_REQUEST, "INVALID_06", "Invalid date format (yyyy/mm/dd)"),
    DATE_PAST(HttpStatus.BAD_REQUEST, "INVALID_07", "Date must be in the past"),
    DATE_FUTURE(HttpStatus.BAD_REQUEST, "INVALID_07", "Date must be in the future"),
    ;

    HttpStatus httpStatus;
    String code;
    String message;

}
