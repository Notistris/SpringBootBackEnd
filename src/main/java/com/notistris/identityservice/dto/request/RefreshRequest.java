package com.notistris.identityservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshRequest {

    @NotBlank(message = "FIELD_BLANK")
    String refreshToken;

}
