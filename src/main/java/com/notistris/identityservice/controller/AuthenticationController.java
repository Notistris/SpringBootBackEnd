package com.notistris.identityservice.controller;

import java.text.ParseException;
import java.util.Arrays;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;
import com.notistris.identityservice.dto.request.AuthenticationRequest;
import com.notistris.identityservice.dto.request.IntrospectRequest;
import com.notistris.identityservice.dto.response.ApiResponse;
import com.notistris.identityservice.dto.response.AuthResult;
import com.notistris.identityservice.dto.response.AuthenticationResponse;
import com.notistris.identityservice.dto.response.IntrospectResponse;
import com.notistris.identityservice.enums.AuthErrorCode;
import com.notistris.identityservice.exception.AppException;
import com.notistris.identityservice.service.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @RequestBody @Valid AuthenticationRequest authenticationRequest) throws JOSEException {
        AuthResult<AuthenticationResponse> authResult = authenticationService.authenticate(authenticationRequest);
        ApiResponse<AuthenticationResponse> apiResponse = ApiResponse.success(authResult.getResponse());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.getResponseCookie().toString())
                .body(apiResponse);
    }

    @PostMapping("/introspect")
    public ResponseEntity<ApiResponse<IntrospectResponse>> introspect(
            @RequestBody @Valid IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        ApiResponse<IntrospectResponse> apiResponse =
                ApiResponse.success(authenticationService.introspect(introspectRequest));
        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) throws ParseException, JOSEException {
        // Đọc refresh token từ cookie
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AppException(AuthErrorCode.UNAUTHORIZED));

        AuthResult<String> authResult = authenticationService.logout(refreshToken);
        ApiResponse<String> apiResponse = ApiResponse.success(authResult.getResponse());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.getResponseCookie().toString())
                .body(apiResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refresh(HttpServletRequest request)
            throws ParseException, JOSEException {
        // Đọc refresh token từ cookie
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AppException(AuthErrorCode.UNAUTHORIZED));

        AuthResult<AuthenticationResponse> authResult = authenticationService.refreshToken(refreshToken);
        ApiResponse<AuthenticationResponse> apiResponse = ApiResponse.success(authResult.getResponse());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResult.getResponseCookie().toString())
                .body(apiResponse);
    }
}
