package com.ai.edumindaiapi.common.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatConversationRequest(
        @NotBlank String title
) {}
