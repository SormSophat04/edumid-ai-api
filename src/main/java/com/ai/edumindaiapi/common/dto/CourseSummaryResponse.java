package com.ai.edumindaiapi.common.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSummaryResponse {
    private Long id;
    private String title;
    private String category;
    private String difficulty;
    private String imageUrl;
    private String description;
    private int progress;
}
