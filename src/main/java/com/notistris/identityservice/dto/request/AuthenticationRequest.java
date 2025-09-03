package com.notistris.identityservice.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {

    @NotBlank(message = "USER_BLANK")
    @Size(min = 3, message = "USER_INVALID")
    String username;

    @NotBlank(message = "PASSWORD_BLANK")
    @Size(min = 5, message = "PASSWORD_INVALID")
    String password;

}
