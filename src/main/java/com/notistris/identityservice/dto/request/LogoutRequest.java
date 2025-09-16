package com.notistris.identityservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogoutRequest {

    @NotBlank(message = "FIELD_BLANK")
    String token;

}
