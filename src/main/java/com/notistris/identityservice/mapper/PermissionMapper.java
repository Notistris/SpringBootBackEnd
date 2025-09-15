package com.notistris.identityservice.mapper;

import com.notistris.identityservice.dto.request.PermissionRequest;
import com.notistris.identityservice.dto.response.PermissionResponse;
import com.notistris.identityservice.entity.Permission;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);

    List<PermissionResponse> toPermissonResponseList(List<Permission> permissionList);

}
