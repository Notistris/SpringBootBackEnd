package com.notistris.identityservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.notistris.identityservice.exception.ErrorCode;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
class ErrorResponse {

    String code;
    String message;

}

@Getter
@Setter
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
abstract class BaseResponse<T> {

    boolean success;
    ErrorResponse error;
    T data;

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