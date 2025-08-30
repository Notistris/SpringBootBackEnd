package com.notistris.identityservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class UserCreationRequest {
    @NotBlank(message = "USER_BLANK")
    @Size(min = 3, message = "USER_INVALID")
    private String username;

    @NotBlank(message = "PASSWORD_BLANK")
    @Size(min = 8, message = "PASSWORD_INVALID")
    private String password;

    @NotBlank(message = "FIRSTNAME_BLANK")
    private String firstName;

    @NotBlank(message = "LASTNAME_BLANK")
    private String lastName;

    @Past(message = "DATE_PAST")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dob;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
}
