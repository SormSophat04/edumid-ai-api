package com.ai.edumindaiapi.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record QuizGenerateRequest(
        @NotBlank String topic,
        @NotBlank String difficulty,
        @Min(1) int count
) {}
