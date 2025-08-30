package com.notistris.identityservice.controller;

import com.notistris.identityservice.dto.request.ApiResponse;
import com.notistris.identityservice.dto.request.UserCreationRequest;
import com.notistris.identityservice.dto.request.UserUpdateRequest;
import com.notistris.identityservice.entity.User;
import com.notistris.identityservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.success(userService.createUser(request));
    }

    @GetMapping
    public ApiResponse<List<User>> getUsers() {
        return ApiResponse.success(userService.getUsers());
    }

    @GetMapping("/{userId}")
    public ApiResponse<User> getUser(@PathVariable String userId) {
        return ApiResponse.success(userService.getUser(userId));
    }

    @PutMapping("/{userId}")
    public ApiResponse<User> updateUser(@PathVariable String userId,
            @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.success(userService.updateUser(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.success("User has been deleted");
    }

    @DeleteMapping
    public ApiResponse<String> deleteAllUsers() {
        userService.deleteAllUsers();
        return ApiResponse.success("All users have been deleted");
    }
}
