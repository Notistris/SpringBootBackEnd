package com.notistris.identityservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.notistris.identityservice.dto.request.RoleRequest;
import com.notistris.identityservice.dto.response.RoleResponse;
import com.notistris.identityservice.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);

    List<RoleResponse> toRoleResponseList(List<Role> rolesList);
}
