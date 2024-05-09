package com.enigma.wmb_api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.response.auth.JwtClaims;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
    private final String JWT_SECRET;
    private final String ISSUER;
    private final long JWT_EXPIRATION;

    // Dependency Inject from environment
    public JwtServiceImpl(
            @Value("${wmb_api.jwt.secret_key}") String jwtSecret,
            @Value("${wmb_api.jwt.issuer}") String issuer,
            @Value("${wmb_api.jwt.expirationInSecond}") long expiration
    ) {
        JWT_SECRET = jwtSecret;
        ISSUER = issuer;
        JWT_EXPIRATION = expiration;
    }

    // Generate Token Service
    @Override
    public String generateToken(UserAccount userAccount) {
        try {
            // Create Algorithm (HMAC512) to crate JWT sign
            Algorithm algorithm = Algorithm.HMAC512(JWT_SECRET);

            // Create JWT Token
            return JWT.create()
                    .withSubject(userAccount.getId())
                    .withClaim("roles", userAccount.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plusSeconds(JWT_EXPIRATION))
                    .withIssuer(ISSUER)
                    .sign(algorithm);

        } catch (JWTCreationException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ResponseMessage.ERROR_CREATING_JWT);
        }
    }

    // Verify Token Service
    @Override
    public boolean verifyJwtToken(String bearerToken) {
        try {
            // Create Algorithm (HMAC512) to crate JWT sign
            Algorithm algorithm = Algorithm.HMAC512(JWT_SECRET);

            // Create JWT Verifier with algorithm to verify JWT
            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();

            // Verify token with parse bearer token to token
            jwtVerifier.verify(parseJwt(bearerToken));

            return true;
        } catch (JWTVerificationException e) {
            // Logger for error
            log.error("Invalid JWT Signature/Claims : {}", e.getMessage());
            return false;
        }
    }

    // Get Claims by Token Service
    @Override
    public JwtClaims getClaimsByToken(String bearerToken) {
        try {
            // Create Algorithm (HMAC512) to crate JWT sign
            Algorithm algorithm = Algorithm.HMAC512(JWT_SECRET);

            // Create JWT Verifier with algorithm to verify JWT
            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();

            // Decoded JWT & Verify token with parse bearer token to token
            DecodedJWT decodedJWT = jwtVerifier.verify(parseJwt(bearerToken));

            // Create JWT Claims
            return JwtClaims.builder()
                    .userAccountId(decodedJWT.getSubject())
                    // Get Claim from decoded JWT
                    .roles(decodedJWT.getClaim("roles").asList(String.class))
                    .build();
        } catch (JWTVerificationException e) {
            // Logger for error
            log.error("Invalid JWT Signature/Claims : {}", e.getMessage());
            return null;
        }
    }

    // Parse Jwt from Bearer Token
    private String parseJwt(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
