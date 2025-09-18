package com.notistris.identityservice.exception;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.notistris.identityservice.dto.request.UserCreationRequest;
import com.notistris.identityservice.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class GlobalExceptionTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UserCreationRequest request;

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
    }

    @Test
    void requestBody_missingFields_fail() throws Exception {
        // GIVEN
        request.setDob(null);
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("error.message").value("Request body missing fields"));
    }

    @Test
    void dob_tooYoung_fail() throws Exception {
        // GIVEN
        request.setDob(LocalDate.now().minusYears(10));
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("error.message").value("Your age must be at least 12"));
    }

    @Test
    void dob_invalidFormat_fail() throws Exception {
        // GIVEN: gửi raw JSON với dob sai format
        String invalidDobJson =
                """
				{
				"username": "nottt",
				"password": "12345",
				"firstName": "Tri",
				"lastName": "Tran",
				"dob": "01-01-2002"
				}
				""";

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(invalidDobJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("error.message").value("Invalid date format (yyyy/mm/dd)"));
    }

    @Test
    void request_missingBody_fail() throws Exception {
        // GIVEN
        String requestBody = """
				""";
        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("error.message").value("Required request body is missing"));
    }

    @Test
    void requestBody_invalidFormat_fail() throws Exception {
        // GIVEN
        String requestBody =
                """
				{
				"username": nottt
				"password": "12345",
				"firstName": "Tri",
				"lastName": "Tran",
				"dob": "2002-01-01"
				}
				""";
        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("error.message").value("Invalid body format"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void method_notAllowed_fail() throws Exception {
        // GIVEN

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users/myInfo") // not allow in path "/auth/login"
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("error.message").value("Method is not allowed"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void path_notFound_fail() throws Exception {
        // GIVEN

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/abcdef") // not allow in path "/auth/login"
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("error.message").value("Path not found"));
    }
}
