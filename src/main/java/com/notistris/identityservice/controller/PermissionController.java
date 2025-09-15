package com.notistris.identityservice.controller;


import com.notistris.identityservice.dto.request.PermissionRequest;
import com.notistris.identityservice.dto.response.ApiResponse;
import com.notistris.identityservice.dto.response.PermissionResponse;
import com.notistris.identityservice.service.PermissionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {

    PermissionService permissionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> create(@RequestBody @Valid PermissionRequest request) {
        ApiResponse<PermissionResponse> apiResponse = ApiResponse.success(permissionService.create(request));
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAll() {
        ApiResponse<List<PermissionResponse>> apiResponse = ApiResponse.success(permissionService.getAll());
        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/{permission}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable(name = "permission") String permission) {
        permissionService.deletePermission(permission);
        ApiResponse<String> apiResponse = ApiResponse.success("Permission has been deleted");
        return ResponseEntity.ok(apiResponse);
    }
}
