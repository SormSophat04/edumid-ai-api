package com.ai.edumindaiapi.common.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCourseRequest(
        @NotBlank String title,
        String description,
        String category,
        String difficulty,
        String imageUrl
) {}
