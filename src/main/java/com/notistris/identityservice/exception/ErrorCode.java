package com.notistris.identityservice.exception;

public interface ErrorCode {

    String code = "ERROR_00";
    String message = "";

    String getCode();

    String getMessage();

}
