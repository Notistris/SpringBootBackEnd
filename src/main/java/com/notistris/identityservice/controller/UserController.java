package com.notistris.identityservice.controller;

import com.notistris.identityservice.dto.request.UserCreationRequest;
import com.notistris.identityservice.dto.request.UserUpdateRequest;
import com.notistris.identityservice.dto.response.ApiResponse;
import com.notistris.identityservice.dto.response.UserResponse;
import com.notistris.identityservice.service.UserService;
import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<UserResponse> apiResponse = ApiResponse.success(userService.createUser(request));
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsers() {
        ApiResponse<List<UserResponse>> apiResponse = ApiResponse.success(userService.getUsers());
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String userId) {
        ApiResponse<UserResponse> apiResponse = ApiResponse.success(userService.getUser(userId));
        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable String userId,
            @RequestBody @Valid UserUpdateRequest request) {
        ApiResponse<UserResponse> apiResponse = ApiResponse.success(userService.updateUser(userId, request));
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
