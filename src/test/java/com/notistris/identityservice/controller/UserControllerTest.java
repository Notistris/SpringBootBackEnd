package com.notistris.identityservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.notistris.identityservice.dto.request.UserCreationRequest;
import com.notistris.identityservice.dto.response.UserResponse;
import com.notistris.identityservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    private UserCreationRequest request;
    private UserResponse response;

    @BeforeEach
    void initData() {
        LocalDate dob = LocalDate.of(2002, 1, 1);

        request = UserCreationRequest.builder()
                .username("nottt")
                .password("12345")
                .firstName("Tri")
                .lastName("Tran")
                .dob(dob)
                .build();

        response = UserResponse.builder()
                .id("38564fa1dab")
                .username("nottt")
                .firstName("Tri")
                .lastName("Tran")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        // GIVEN
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        Mockito.when(userService.createUser(ArgumentMatchers.any()))
                .thenReturn(response);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.id").value("38564fa1dab")
                );
    }

    @Test
    void createUser_usernameInvalid_fail() throws Exception {
        // GIVEN
        request.setUsername("aaa");
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("error.message").value("Username must be at least 5 characters")
                );
    }

    @Test
    void createUser_usernameNull_fail() throws Exception {
        // GIVEN
        request.setUsername(null);
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("error.message").value("Request body missing fields")
                );
    }

    @Test
    void createUser_dobNull_fail() throws Exception {
        // GIVEN
        request.setDob(null);
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("error.message").value("Request body missing fields")
                );
    }

    @Test
    void createUser_dobTooYoung_fail() throws Exception {
        // GIVEN
        request.setDob(LocalDate.now().minusYears(10));
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("error.message").value("Your age must be at least 12")
                );
    }

    @Test
    void createUser_dobInvalidFormat_fail() throws Exception {
        // GIVEN: gửi raw JSON với dob sai format
        String invalidDobJson = """
                {
                  "username": "nottt",
                  "password": "12345",
                  "firstName": "Tri",
                  "lastName": "Tran",
                  "dob": "01-01-2002"
                }
                """;

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(invalidDobJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("error.message")
                        .value("Invalid date format (yyyy/mm/dd)"));
    }

}
