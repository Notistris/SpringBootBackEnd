package com.notistris.identityservice.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.notistris.identityservice.dto.request.UserCreationRequest;
import com.notistris.identityservice.dto.response.UserResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class UserControllerIntegrationTest {

    @Container
    static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>("mariadb:latest");

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private UserCreationRequest request;
    private UserResponse response;

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MARIA_DB_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MARIA_DB_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MARIA_DB_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.mariadb.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

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

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.username").value("nottt"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.firstName").value("Tri"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.lastName").value("Tran"));
    }
}
