package com.notistris.identityservice.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.notistris.identityservice.entity.Permission;
import com.notistris.identityservice.entity.Role;
import com.notistris.identityservice.entity.User;
import com.notistris.identityservice.enums.RoleEnum;
import com.notistris.identityservice.repository.PermissionRepository;
import com.notistris.identityservice.repository.RoleRepository;
import com.notistris.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "org.mariadb.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (permissionRepository.count() == 0) {
                permissionRepository.saveAll(List.of(
                        Permission.builder()
                                .name("READ_DATA")
                                .description("Read data")
                                .build(),
                        Permission.builder()
                                .name("CREATE_POST")
                                .description("Create a post")
                                .build(),
                        Permission.builder()
                                .name("REJECT_POST")
                                .description("Reject a post")
                                .build()));
                log.info("Default permissions created");
            }

            if (roleRepository.count() == 0) {
                Role adminRole = Role.builder()
                        .name(RoleEnum.ADMIN.name())
                        .description("Admin role")
                        .permissions(new HashSet<>(permissionRepository.findAll()))
                        .build();
                Role userRole = Role.builder()
                        .name(RoleEnum.USER.name())
                        .description("User role")
                        .permissions(
                                new HashSet<>(permissionRepository.findAllById(Collections.singleton("CREATE_POST"))))
                        .build();

                roleRepository.saveAll(List.of(adminRole, userRole));
                log.info("Default roles created");
            }

            if (userRepository.findByUsername("admin").isEmpty()) {
                List<Role> roles = roleRepository.findAllById(Collections.singleton(RoleEnum.ADMIN.name()));

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(new HashSet<>(roles))
                        .build();

                userRepository.save(user);
                log.warn("Admin user has been created with default password: admin, please change it");
            }
        };
    }
}
