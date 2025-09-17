package com.notistris.identityservice.service;

import com.notistris.identityservice.dto.request.RoleRequest;
import com.notistris.identityservice.dto.response.RoleResponse;
import com.notistris.identityservice.entity.Permission;
import com.notistris.identityservice.entity.Role;
import com.notistris.identityservice.mapper.RoleMapper;
import com.notistris.identityservice.repository.PermissionRepository;
import com.notistris.identityservice.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {

    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse create(RoleRequest request) {
        Role role = roleMapper.toRole(request);

        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> getAll() {
        return roleMapper.toRoleResponseList(roleRepository.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRole(String role) {
        roleRepository.deleteById(role);
    }
}
