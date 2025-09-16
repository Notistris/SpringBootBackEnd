package com.notistris.identityservice.enums;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    String code = "ERROR_00";
    String message = "";

    String getCode();

    String getMessage();

    HttpStatus getHttpStatus();
}
