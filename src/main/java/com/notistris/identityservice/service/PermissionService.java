package com.notistris.identityservice.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.notistris.identityservice.dto.request.PermissionRequest;
import com.notistris.identityservice.dto.response.PermissionResponse;
import com.notistris.identityservice.entity.Permission;
import com.notistris.identityservice.mapper.PermissionMapper;
import com.notistris.identityservice.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponse> getAll() {
        return permissionMapper.toPermissonResponseList(permissionRepository.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deletePermission(String permission) {
        permissionRepository.deleteById(permission);
    }
}
