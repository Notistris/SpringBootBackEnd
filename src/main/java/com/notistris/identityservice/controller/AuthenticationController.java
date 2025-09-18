package com.notistris.identityservice.controller;

import java.text.ParseException;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;
import com.notistris.identityservice.dto.request.AuthenticationRequest;
import com.notistris.identityservice.dto.request.IntrospectRequest;
import com.notistris.identityservice.dto.request.LogoutRequest;
import com.notistris.identityservice.dto.request.RefreshRequest;
import com.notistris.identityservice.dto.response.ApiResponse;
import com.notistris.identityservice.dto.response.AuthenticationResponse;
import com.notistris.identityservice.dto.response.IntrospectResponse;
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
        ApiResponse<AuthenticationResponse> apiResponse =
                ApiResponse.success(authenticationService.authenticate(authenticationRequest));
        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/introspect")
    public ResponseEntity<ApiResponse<IntrospectResponse>> authenticate(
            @RequestBody @Valid IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        ApiResponse<IntrospectResponse> apiResponse =
                ApiResponse.success(authenticationService.introspect(introspectRequest));
        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody @Valid LogoutRequest logoutRequest)
            throws ParseException, JOSEException {
        authenticationService.logout(logoutRequest);
        ApiResponse<String> apiResponse = ApiResponse.success(null);
        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refresh(
            @RequestBody @Valid RefreshRequest refreshRequest) throws ParseException, JOSEException {
        ApiResponse<AuthenticationResponse> apiResponse =
                ApiResponse.success(authenticationService.refreshToken(refreshRequest));
        return ResponseEntity.ok().body(apiResponse);
    }
}
