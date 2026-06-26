package com.ai.edumindaiapi.common.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationResponse {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
}
