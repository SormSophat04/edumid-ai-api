package com.ai.edumindaiapi.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-ms:3600000}")
    private long expirationMs;

    @Value("${security.jwt.issuer:clot-api}")
    private String issuer;

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
                .signWith(signingKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signingKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            String paddedSecret = secret;
            while (paddedSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
                paddedSecret += "0";
            }
            return Keys.hmacShaKeyFor(paddedSecret.getBytes(StandardCharsets.UTF_8));
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
