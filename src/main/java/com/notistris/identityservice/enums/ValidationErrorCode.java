package com.notistris.identityservice.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ValidationErrorCode implements ErrorCode {

    MESSAGE_KEY_INVALID("INVALID_00", "Invalid message key"),

    FIELD_BLANK("INVALID_00", "Invalid request body"),

    USER_INVALID("INVALID_02", "Username must be at least 3 characters"),
    PASSWORD_INVALID("INVALID_03", "Password must be at least 5 characters"),

    DATE_INVALID("INVALID_04", "Invalid date format (yyyy/mm/dd)"),
    DATE_PAST("INVALID_04", "Date must be in the past"),
    DATE_FUTURE("INVALID_04", "Date must be in the future");

    String code;
    String message;

}
