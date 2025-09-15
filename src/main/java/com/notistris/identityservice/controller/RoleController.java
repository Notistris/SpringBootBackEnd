package com.notistris.identityservice.controller;


import com.notistris.identityservice.dto.request.RoleRequest;
import com.notistris.identityservice.dto.response.ApiResponse;
import com.notistris.identityservice.dto.response.RoleResponse;
import com.notistris.identityservice.service.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> create(@RequestBody @Valid RoleRequest request) {
        ApiResponse<RoleResponse> apiResponse = ApiResponse.success(roleService.create(request));
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAll() {
        ApiResponse<List<RoleResponse>> apiResponse = ApiResponse.success(roleService.getAll());
        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/{role}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable(name = "role") String role) {
        roleService.deleteRole(role);
        ApiResponse<String> apiResponse = ApiResponse.success("Role has been deleted");
        return ResponseEntity.ok(apiResponse);
    }
}
