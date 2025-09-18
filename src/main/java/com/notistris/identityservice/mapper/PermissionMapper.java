package com.notistris.identityservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.notistris.identityservice.dto.request.PermissionRequest;
import com.notistris.identityservice.dto.response.PermissionResponse;
import com.notistris.identityservice.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(target = "roles", ignore = true)
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);

    List<PermissionResponse> toPermissonResponseList(List<Permission> permissionList);
}
