package com.ai.edumindaiapi.common.dto;

import lombok.Builder;

@Builder
public record AuthResponse(
        String token,
        Long userId,
        String name,
        String email,
        String role
) {}
