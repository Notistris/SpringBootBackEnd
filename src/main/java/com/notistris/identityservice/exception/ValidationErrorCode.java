package com.notistris.identityservice.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ValidationErrorCode implements ErrorCode {

    MESSAGE_KEY_INVALID("INVALID_00", "Invalid message key"),

    USER_BLANK("INVALID_01", "Username must not be blank"),
    PASSWORD_BLANK("INVALID_01", "Password must not be blank"),
    FIRSTNAME_BLANK("INVALID_01", "FirstName must not be blank"),
    LASTNAME_BLANK("INVALID_01", "LastName must not be blank"),
    DATE_BLANK("INVALID_01", "Date must not be blank"),

    USER_INVALID("INVALID_02", "Username must be at least 3 characters"),
    PASSWORD_INVALID("INVALID_03", "Password must be at least 8 characters"),

    DATE_INVALID("INVALID_04", "Invalid date format (yyyy/mm/dd)"),
    DATE_PAST("INVALID_04", "Date must be in the past"),
    DATE_FUTURE("INVALID_04", "Date must be in the future");

    String code;
    String message;

}
