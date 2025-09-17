package com.notistris.identityservice.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import com.notistris.identityservice.dto.response.ApiResponse;
import com.notistris.identityservice.enums.AuthErrorCode;
import com.notistris.identityservice.enums.ErrorCode;
import com.notistris.identityservice.repository.InvalidatedTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtBlacklistFilter extends OncePerRequestFilter {

    InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                SignedJWT signedJWT = SignedJWT.parse(token);
                String jti = signedJWT.getJWTClaimsSet().getJWTID();

                if (invalidatedTokenRepository.existsById(jti)) {
                    ApiResponse<ErrorCode> apiResponse = ApiResponse.error(AuthErrorCode.UNAUTHORIZED);

                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
                    return;
                }


            } catch (ParseException e) {
                ApiResponse<ErrorCode> apiResponse = ApiResponse.error(AuthErrorCode.UNAUTHORIZED);
                
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

}
