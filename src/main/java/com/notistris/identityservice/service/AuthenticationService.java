package com.notistris.identityservice.service;

import com.notistris.identityservice.dto.request.AuthenticationRequest;
import com.notistris.identityservice.dto.response.AuthenticationResponse;
import com.notistris.identityservice.entity.User;
import com.notistris.identityservice.exception.AppException;
import com.notistris.identityservice.exception.AuthErrorCode;
import com.notistris.identityservice.exception.UserErrorCode;
import com.notistris.identityservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        User user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_EXISTS));

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isAuthenticated =
                passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

        if (!isAuthenticated) throw new AppException(AuthErrorCode.INCORRECT_CREDENTIALS);

        return AuthenticationResponse.builder()
                .token("aÂ")
                .refresh_token("b")
                .duration(10)
                .build();
    }

}
