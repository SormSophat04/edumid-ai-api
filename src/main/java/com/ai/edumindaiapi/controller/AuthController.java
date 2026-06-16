package com.ai.edumindaiapi.controller;

import com.ai.edumindaiapi.common.dto.ApiResponse;
import com.ai.edumindaiapi.common.dto.AuthResponse;
import com.ai.edumindaiapi.common.dto.LoginRequest;
import com.ai.edumindaiapi.common.dto.RegisterRequest;
import com.ai.edumindaiapi.common.exception.BadRequestException;
import com.ai.edumindaiapi.domain.User;
import com.ai.edumindaiapi.jwt.JwtService;
import com.ai.edumindaiapi.mapper.UserMapper;
import com.ai.edumindaiapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);

        String token = jwtService.generateToken(user.getEmail(), user.getId(), List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));

        AuthResponse authResponse = userMapper.toAuthResponse(user, token);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new BadRequestException("User not found"));

            String token = jwtService.generateToken(user.getEmail(), user.getId(), List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));

            AuthResponse authResponse = userMapper.toAuthResponse(user, token);

            return ResponseEntity.ok(ApiResponse.ok("Login successful", authResponse));
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid email or password");
        }
    }
}
