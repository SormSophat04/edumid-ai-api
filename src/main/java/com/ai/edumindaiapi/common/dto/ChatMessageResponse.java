package com.ai.edumindaiapi.common.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private String sender;
    private String text;
    private LocalDateTime createdAt;
}
