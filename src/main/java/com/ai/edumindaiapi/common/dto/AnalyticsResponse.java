package com.ai.edumindaiapi.common.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private List<PerformanceEntry> performanceHistory;
    private List<SubjectStrength> subjectStrengths;
    private List<StudyHourEntry> studyHours;
    private GradePrediction aiPrediction;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceEntry {
        private String week;
        private int score;
        private int avg;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectStrength {
        private String subject;
        private int score;
        private int limit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudyHourEntry {
        private String day;
        private double hours;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GradePrediction {
        private String grade;
        private int confidence;
        private List<String> insights;
    }
}
