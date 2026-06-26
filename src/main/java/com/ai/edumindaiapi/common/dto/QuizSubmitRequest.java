package com.ai.edumindaiapi.common.dto;

import jakarta.validation.constraints.NotNull;

public record QuizSubmitRequest(
        @NotNull Long attemptId,
        String answersJson
) {}
