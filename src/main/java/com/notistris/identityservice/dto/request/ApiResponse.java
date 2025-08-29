package com.notistris.identityservice.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.notistris.identityservice.exception.ErrorCode;

class ErrorResponse {
    private String code;
    private String message;


    ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

abstract class BaseResponse<T> {
    private boolean success;
    private ErrorResponse error;
    private T data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> extends BaseResponse<T> {
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setError(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
        return response;
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setError(new ErrorResponse(code, message));
        return response;
    }
}