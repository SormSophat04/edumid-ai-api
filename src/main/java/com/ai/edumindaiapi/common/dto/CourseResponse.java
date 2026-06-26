package com.ai.edumindaiapi.common.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String difficulty;
    private String imageUrl;
    private Long teacherId;
    private int progress;
    private List<ModuleDto> modules;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModuleDto {
        private Long id;
        private String title;
        private int orderIndex;
        private boolean completed;
        private List<LessonDto> lessons;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonDto {
        private Long id;
        private String title;
        private String type;
        private String duration;
        private String videoUrl;
        private String content;
        private int orderIndex;
        private boolean active;
        private boolean completed;
    }
}
