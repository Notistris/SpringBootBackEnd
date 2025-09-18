package com.notistris.identityservice.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {

    @NotBlank(message = "FIELD_BLANK")
    String name;

    @NotBlank(message = "FIELD_BLANK")
    String description;

    @NotEmpty(message = "FIELD_BLANK")
    Set<String> permissions;
}
