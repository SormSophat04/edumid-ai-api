package com.ai.edumindaiapi.common.dto;

import jakarta.validation.constraints.NotNull;

public record LessonCompleteRequest(
        @NotNull Long lessonId
) {}
