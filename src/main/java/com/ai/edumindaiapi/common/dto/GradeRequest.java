package com.ai.edumindaiapi.common.dto;

import jakarta.validation.constraints.NotNull;

public record GradeRequest(
        @NotNull Integer score,
        String feedbackJson
) {}
