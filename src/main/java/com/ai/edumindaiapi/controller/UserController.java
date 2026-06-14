package com.ai.edumindaiapi.controller;

import com.ai.edumindaiapi.common.dto.ApiResponse;
import com.ai.edumindaiapi.common.dto.UserResponse;
import com.ai.edumindaiapi.common.exception.ResourceNotFoundException;
import com.ai.edumindaiapi.domain.User;
import com.ai.edumindaiapi.mapper.UserMapper;
import com.ai.edumindaiapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));

        return ResponseEntity.ok(ApiResponse.ok(userMapper.toUserResponse(user)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id,
            Authentication authentication) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        User currentUser = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));

        if (!"ADMIN".equals(currentUser.getRole().name()) && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Access denied");
        }

        return ResponseEntity.ok(ApiResponse.ok(userMapper.toUserResponse(user)));
    }
}
