package com.notistris.identityservice.config;

import com.notistris.identityservice.entity.Role;
import com.notistris.identityservice.entity.User;
import com.notistris.identityservice.enums.RoleEnum;
import com.notistris.identityservice.repository.RoleRepository;
import com.notistris.identityservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    @Bean
    @ConditionalOnProperty(prefix = "spring", value = "datasource.driverClassName",
            havingValue = "org.mariadb.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
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
