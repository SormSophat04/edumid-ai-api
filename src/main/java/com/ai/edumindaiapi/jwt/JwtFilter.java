package com.ai.edumindaiapi.jwt;

import com.ai.edumindaiapi.security.AuthUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.replace("Bearer ", "");

        try {
            Claims claims = jwtService.parseToken(token);
            String username = claims.getSubject();
            Object authoritiesClaim = claims.get("authorities");

            List<SimpleGrantedAuthority> simpleGrantedAuthorities = List.of();
            if (authoritiesClaim instanceof Collection<?> values) {
                simpleGrantedAuthorities = values.stream()
                        .map(Object::toString)
                        .map(SimpleGrantedAuthority::new)
                        .toList();
            }

            Long userId = claims.get("userId", Long.class);

            AuthUser authUser = AuthUser.builder()
                    .id(userId)
                    .username(username)
                    .authorities(simpleGrantedAuthorities)
                    .build();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authUser,
                    null,
                    simpleGrantedAuthorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

}
