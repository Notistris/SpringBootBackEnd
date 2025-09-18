package com.notistris.identityservice.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest {

    @NotBlank(message = "FIELD_BLANK")
    String name;

    @NotBlank(message = "FIELD_BLANK")
    String description;
}
