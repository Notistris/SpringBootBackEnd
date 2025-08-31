package com.notistris.identityservice.controller;

import com.notistris.identityservice.dto.request.ApiResponse;
import com.notistris.identityservice.dto.request.UserCreationRequest;
import com.notistris.identityservice.dto.request.UserUpdateRequest;
import com.notistris.identityservice.entity.User;
import com.notistris.identityservice.service.UserService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<User> apiResponse = ApiResponse.success(userService.createUser(request));
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getUsers() {
        ApiResponse<List<User>> apiResponse = ApiResponse.success(userService.getUsers());
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable String userId) {
        ApiResponse<User> apiResponse = ApiResponse.success(userService.getUser(userId));
        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable String userId,
            @RequestBody @Valid UserUpdateRequest request) {
        ApiResponse<User> apiResponse = ApiResponse.success(userService.updateUser(userId, request));
        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        ApiResponse<String> apiResponse = ApiResponse.success("User has been deleted");
        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteAllUsers() {
        userService.deleteAllUsers();
        ApiResponse<String> apiResponse = ApiResponse.success("All users have been deleted");
        return ResponseEntity.ok().body(apiResponse);
    }
}
