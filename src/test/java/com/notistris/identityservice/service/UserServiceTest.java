package com.notistris.identityservice.service;

import com.notistris.identityservice.dto.request.UserCreationRequest;
import com.notistris.identityservice.dto.response.UserResponse;
import com.notistris.identityservice.entity.User;
import com.notistris.identityservice.exception.AppException;
import com.notistris.identityservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@EnableMethodSecurity
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    private UserCreationRequest request;
    private User user;

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

        user = User.builder()
                .id("38564fa1dab")
                .username("nottt")
                .password("12345")
                .firstName("Tri")
                .lastName("Tran")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        // GIVEN
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString()))
                .thenReturn(false);

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(user);

        // WHEN
        UserResponse response = userService.createUser(request);

        // THEN
        assertThat(response.getId()).isEqualTo("38564fa1dab");
        assertThat(response.getUsername()).isEqualTo("nottt");
    }

    @Test
    void createUser_userExisted_fail() {
        // GIVEN
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString()))
                .thenReturn(true);

        // WHEN, THEN
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode().getCode())
                .isEqualTo("USER_02");
    }

    @Test
    @WithMockUser(username = "123", roles = "ADMIN")
    void deleteUser_success() {
        // GIVEN
        String userId = "123";

        // WHEN
        userService.deleteUser(userId);

        // THEN
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }


}

