package com.ai.edumindaiapi.common.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponse {
    private Long id;
    private String title;
    private String courseName;
    private String dueDate;
    private String status;
    private Integer score;
    private FeedbackDto feedback;
    private String fileUrl;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackDto {
        private int grammar;
        private int logic;
        private int completeness;
        private String text;
    }
}
