package com.notistris.identityservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.notistris.identityservice.dto.request.UserCreationRequest;
import com.notistris.identityservice.dto.response.UserResponse;
import com.notistris.identityservice.entity.Permission;
import com.notistris.identityservice.entity.Role;
import com.notistris.identityservice.entity.User;
import com.notistris.identityservice.enums.RoleEnum;
import com.notistris.identityservice.exception.AppException;
import com.notistris.identityservice.repository.RoleRepository;
import com.notistris.identityservice.repository.UserRepository;

@SpringBootTest
@EnableMethodSecurity
@TestPropertySource("/test.properties")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RoleRepository roleRepository;

    private UserCreationRequest request;
    private User user;
    private List<Role> roles;

    @BeforeEach
    void initData() {
        LocalDate dob = LocalDate.of(2002, 1, 1);

        List<Permission> permissions = List.of(Permission.builder()
                .name("CREATE_POST")
                .description("Create a post")
                .build());

        roles = List.of(Role.builder()
                .name(RoleEnum.USER.name())
                .description("User role")
                .permissions(new HashSet<>(permissions))
                .build());

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
                .roles(new HashSet<>(roles))
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        // GIVEN
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString()))
                .thenReturn(false);

        Mockito.when(userRepository.save(ArgumentMatchers.any())).thenReturn(user);

        Mockito.when(roleRepository.findAllById(ArgumentMatchers.any())).thenReturn(roles);

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
                .extracting(e -> ((AppException) e).getErrorCode().getMessage())
                .isEqualTo("User already exists");
    }

    @Test
    @WithMockUser(username = "38564fa1dab", roles = "ADMIN")
    void getMyInfo_valid_success() {
        // GIVEN
        Mockito.when(userRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(user));

        // WHEN
        UserResponse response = userService.getMyInfo();

        // THEN
        assertThat(response.getUsername()).isEqualTo("nottt");
        assertThat(response.getId()).isEqualTo("38564fa1dab");
    }

    @Test
    @WithMockUser(username = "38564fa1dab")
    void getMyInfo_userNotExists_fail() {
        // GIVEN
        Mockito.when(userRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        // WHEN, THEN
        assertThatThrownBy(() -> userService.getMyInfo())
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode().getMessage())
                .isEqualTo("User not exists");
    }

    @Test
    @WithMockUser(username = "38564fa1dab", roles = "ADMIN")
    void deleteUser_success() {
        // GIVEN
        String userId = user.getId();

        // WHEN
        userService.deleteUser(userId);

        // THEN
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }
}
