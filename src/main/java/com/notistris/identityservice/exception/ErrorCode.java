package com.notistris.identityservice.exception;

public interface ErrorCode {
    String code = "ERROR_00";
    String message = "";

    public String getCode();

    public String getMessage();

}
