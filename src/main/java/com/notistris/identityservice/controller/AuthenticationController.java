package com.notistris.identityservice.controller;

import com.nimbusds.jose.JOSEException;
import com.notistris.identityservice.dto.request.AuthenticationRequest;
import com.notistris.identityservice.dto.request.IntrospectRequest;
import com.notistris.identityservice.dto.response.ApiResponse;
import com.notistris.identityservice.dto.response.AuthenticationResponse;
import com.notistris.identityservice.dto.response.IntrospectResponse;
import com.notistris.identityservice.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.text.ParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @RequestBody @Valid AuthenticationRequest authenticationRequest) {
        ApiResponse<AuthenticationResponse> apiResponse = ApiResponse
                .success(authenticationService.authenticate(authenticationRequest));
        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/introspect")
    public ResponseEntity<ApiResponse<IntrospectResponse>> authenticate(
            @RequestBody @Valid IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        ApiResponse<IntrospectResponse> apiResponse = ApiResponse
                .success(authenticationService.introspect(introspectRequest));
        return ResponseEntity.ok().body(apiResponse);
    }

}
