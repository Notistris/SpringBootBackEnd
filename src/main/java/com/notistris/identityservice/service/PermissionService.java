package com.notistris.identityservice.service;

import com.notistris.identityservice.dto.request.PermissionRequest;
import com.notistris.identityservice.dto.response.PermissionResponse;
import com.notistris.identityservice.entity.Permission;
import com.notistris.identityservice.mapper.PermissionMapper;
import com.notistris.identityservice.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    public List<PermissionResponse> getAll() {
        return permissionMapper.toPermissonResponseList(permissionRepository.findAll());
    }

    public void deletePermission(String permission) {
        permissionRepository.deleteById(permission);
    }

}
