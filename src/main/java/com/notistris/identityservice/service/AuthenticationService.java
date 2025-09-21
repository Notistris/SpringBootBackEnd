package com.notistris.identityservice.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.notistris.identityservice.dto.request.AuthenticationRequest;
import com.notistris.identityservice.dto.request.IntrospectRequest;
import com.notistris.identityservice.dto.response.AuthResult;
import com.notistris.identityservice.dto.response.AuthenticationResponse;
import com.notistris.identityservice.dto.response.IntrospectResponse;
import com.notistris.identityservice.entity.InvalidatedToken;
import com.notistris.identityservice.entity.User;
import com.notistris.identityservice.enums.AuthErrorCode;
import com.notistris.identityservice.enums.TokenType;
import com.notistris.identityservice.enums.UserErrorCode;
import com.notistris.identityservice.exception.AppException;
import com.notistris.identityservice.repository.InvalidatedTokenRepository;
import com.notistris.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {

    InvalidatedTokenRepository invalidatedTokenRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public AuthResult<AuthenticationResponse> authenticate(AuthenticationRequest request) throws JOSEException {
        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_EXISTS));

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!isAuthenticated) throw new AppException(AuthErrorCode.INCORRECT_CREDENTIALS);

        return generateAuthResponse(user);
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
        boolean isValid = true;
        try {
            verifyToken(request.getToken());
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthResult<String> logout(String refreshToken) throws ParseException, JOSEException {
        SignedJWT signToken = verifyToken(refreshToken);
        invalidateToken(signToken);

        // xoá cookie refresh token
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true) // bật khi chạy https
                .path("auth") // trùng path với lúc set cookie
                .maxAge(0)
                .build();

        return new AuthResult<>(null, deleteCookie);
    }

    public AuthResult<AuthenticationResponse> refreshToken(String refreshToken) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(refreshToken);

        String tokenType = signedJWT.getJWTClaimsSet().getStringClaim("typ");
        if (!TokenType.REFRESH.name().equals(tokenType)) {
            throw new AppException(AuthErrorCode.UNAUTHORIZED);
        }

        invalidateToken(signedJWT);
        String userId = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_EXISTS));

        return generateAuthResponse(user);
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }

    private String generateAccessToken(User user) throws JOSEException {

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .issuer("com.notistris")
                .claim("typ", TokenType.ACCESS.name())
                .subject(user.getId())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        return signToken(jwtClaimsSet);
    }

    private String generateRefreshToken(User user) throws JOSEException {
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .claim("typ", TokenType.REFRESH.name())
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now()
                        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();

        return signToken(jwtClaimsSet);
    }

    private String signToken(JWTClaimsSet claimsSet) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        jwsObject.sign(new MACSigner(SIGNER_KEY));

        return jwsObject.serialize();
    }

    private SignedJWT verifyToken(String token) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);
        if (!verified || expiryTime.before(new Date())) throw new AppException(AuthErrorCode.UNAUTHORIZED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(AuthErrorCode.UNAUTHORIZED);

        return signedJWT;
    }

    private void invalidateToken(SignedJWT signedJWT) throws ParseException {
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().jti(jti).expiryTime(expiryTime).build();
        invalidatedTokenRepository.save(invalidatedToken);
    }

    private AuthResult<AuthenticationResponse> generateAuthResponse(User user) throws JOSEException {

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true) // nên bật khi dùng https
                .sameSite("Strict")
                .path("auth") // cookie chỉ gửi khi gọi refresh
                .maxAge(REFRESHABLE_DURATION) // thời hạn refreshToken
                .build();

        AuthenticationResponse response = AuthenticationResponse.builder()
                .token(accessToken)
                .duration(VALID_DURATION)
                .build();

        return new AuthResult<>(response, cookie);
    }
}
