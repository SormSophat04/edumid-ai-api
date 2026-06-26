package com.ai.edumindaiapi.common.dto;

import jakarta.validation.constraints.NotNull;

public record CourseProgressRequest(
        @NotNull Long courseId,
        @NotNull Integer progress
) {}
