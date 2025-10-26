package com.kali.sanctum.security.jwt;

import com.kali.sanctum.enums.Token;
import com.kali.sanctum.security.user.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtUtils {
    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;

    @Value("${auth.token.accessExpirationInMils}")
    private int accessExpirationTime;

    @Value("${auth.token.refreshExpirationInMils}")
    private int refreshExpirationTime;

    public String generateTokenForUser(Authentication authentication, Token token, Date absoluteExpiry) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        int expirationTime = token == Token.ACCESS ? accessExpirationTime : refreshExpirationTime;

        String jti = UUID.randomUUID().toString();

        JwtBuilder jwtBuilder = Jwts.builder()
                .id(jti)
                .subject(userPrincipal.getEmail())
                .claim("id", userPrincipal.getId())
                .claim("roles", roles)
                .claim("tokenType", token.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key(), Jwts.SIG.HS256);

        if (token == Token.REFRESH) {
            jwtBuilder.claim("absoluteExpiry", absoluteExpiry);
        }

        return jwtBuilder.compact();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Reusable JWT validation; throws detailed exceptions for service-layer use when invalid.
    public void validateToken(String token) {
        Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token);
    }

    // Quick check validation; returns false instead of throwing, to avoid unnecessary exception handling.
    public boolean isValidToken(String token) {
        try {
            validateToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
