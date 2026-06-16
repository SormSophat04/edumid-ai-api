package com.ai.edumindaiapi.common.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathResponse {
    private List<MilestoneDto> milestones;
    private Recommendation recommendation;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MilestoneDto {
        private Long id;
        private String title;
        private String status;
        private String time;
        private String difficulty;
        private int completion;
        private String focus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recommendation {
        private String title;
        private String description;
    }
}
