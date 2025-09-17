package com.notistris.identityservice.dto.request;

import com.notistris.identityservice.validator.DobConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @NotBlank(message = "FIELD_BLANK")
    @Size(min = 5, message = "USER_INVALID")
    String username;

    @NotBlank(message = "FIELD_BLANK")
    @Size(min = 5, message = "PASSWORD_INVALID")
    String password;

    @NotBlank(message = "FIELD_BLANK")
    String firstName;

    @NotBlank(message = "FIELD_BLANK")
    String lastName;

    @DobConstraint(min = 5, message = "DOB_INVALID")
    @NotNull(message = "FIELD_BLANK")
    LocalDate dob;

}
