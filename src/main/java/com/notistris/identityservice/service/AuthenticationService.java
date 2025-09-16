package com.notistris.identityservice.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.notistris.identityservice.dto.request.AuthenticationRequest;
import com.notistris.identityservice.dto.request.IntrospectRequest;
import com.notistris.identityservice.dto.request.LogoutRequest;
import com.notistris.identityservice.dto.request.RefreshRequest;
import com.notistris.identityservice.dto.response.AuthenticationResponse;
import com.notistris.identityservice.dto.response.IntrospectResponse;
import com.notistris.identityservice.entity.InvalidatedToken;
import com.notistris.identityservice.entity.User;
import com.notistris.identityservice.enums.AuthErrorCode;
import com.notistris.identityservice.enums.UserErrorCode;
import com.notistris.identityservice.exception.AppException;
import com.notistris.identityservice.repository.InvalidatedTokenRepository;
import com.notistris.identityservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

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

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_EXISTS));

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(),
                user.getPassword());

        if (!isAuthenticated)
            throw new AppException(AuthErrorCode.INCORRECT_CREDENTIALS);

        String token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .duration(VALID_DURATION)
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = true;
        try {
            verifyToken(request.getToken(), false);
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public void logout(LogoutRequest request) {
        SignedJWT signToken = verifyToken(request.getToken(), false);
        try {
            String jwtId = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .Id(jwtId)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);

        } catch (ParseException e) {
            throw new AppException(AuthErrorCode.UNAUTHORIZED);
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) {
        SignedJWT signedJWT = verifyToken(request.getToken(), true);

        try {
            String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .Id(jwtId)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);

            String userId = signedJWT.getJWTClaimsSet().getSubject();
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new AppException(UserErrorCode.USER_NOT_EXISTS)
            );

            String token = generateToken(user);

            return AuthenticationResponse.builder()
                    .token(token)
                    .duration(VALID_DURATION)
                    .build();

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .issuer("com.notistris")
                .subject(user.getId())
                .claim("username", user.getUsername())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY));

            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) {
        try {
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiryTime = (isRefresh)
                    ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                    .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                    : signedJWT.getJWTClaimsSet().getExpirationTime();

            boolean verified = signedJWT.verify(verifier);
            if (!verified || expiryTime.before(new Date()))
                throw new AppException(AuthErrorCode.UNAUTHORIZED);

            if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
                throw new AppException(AuthErrorCode.UNAUTHORIZED);

            return signedJWT;
        } catch (JOSEException | ParseException e) {
            throw new AppException(AuthErrorCode.UNAUTHORIZED);
        }
    }

}
