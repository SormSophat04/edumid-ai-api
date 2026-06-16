package com.ai.edumindaiapi.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatSendRequest(
        @NotNull Long conversationId,
        @NotBlank String text
) {}
