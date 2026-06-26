package com.ai.edumindaiapi.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-ms:3600000}")
    private long expirationMs;

    @Value("${security.jwt.issuer:EduMind-AI}")
    private String issuer;

    private SecretKey signingKey;

    @PostConstruct
    void initSigningKey() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret must be configured via security.jwt.secret property");
        }
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes (256 bits) for HS256. Current length: " + keyBytes.length + ". Generate one with: openssl rand -base64 32");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String subject, Long userId, Collection<? extends GrantedAuthority> authorities) {
        List<String> authorityNames = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + expirationMs);

        return Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiration)
                .claim("authorities", authorityNames)
                .claim("userId", userId)
                .signWith(signingKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
