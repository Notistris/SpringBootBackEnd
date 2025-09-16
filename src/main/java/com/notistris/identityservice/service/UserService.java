package com.notistris.identityservice.service;

import com.notistris.identityservice.dto.request.UserCreationRequest;
import com.notistris.identityservice.dto.request.UserUpdateRequest;
import com.notistris.identityservice.dto.response.UserResponse;
import com.notistris.identityservice.entity.Role;
import com.notistris.identityservice.entity.User;
import com.notistris.identityservice.enums.RoleEnum;
import com.notistris.identityservice.enums.UserErrorCode;
import com.notistris.identityservice.exception.AppException;
import com.notistris.identityservice.mapper.UserMapper;
import com.notistris.identityservice.repository.RoleRepository;
import com.notistris.identityservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(UserErrorCode.USER_ALREADY_EXISTS);
        User user = userMapper.toUser(request);

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Default user role
        List<Role> roles = roleRepository.findAllById(Collections.singleton(RoleEnum.USER.name()));
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userMapper.toUserResponseList(userRepository.findAll());
    }

    @PreAuthorize("#userId == authentication.name or hasRole('ADMIN')")
    public UserResponse getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_EXISTS));
        return userMapper.toUserResponse(user);
    }

    @PostAuthorize("returnObject.id == authentication.name")
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_EXISTS));

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("#userId == authentication.name or hasRole('ADMIN')")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_EXISTS));
        userMapper.updateUser(user, request);

        // Encode password if changing password
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // If changing roles
        if (request.getRoles() != null) {
            if (request.getRoles().isEmpty()) {
                user.setRoles(new HashSet<>());
            } else {
                List<Role> roles = roleRepository.findAllById(request.getRoles());
                user.setRoles(new HashSet<>(roles));
            }
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }


    @PreAuthorize("#userId == authentication.name or hasRole('ADMIN')")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

}
