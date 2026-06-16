package com.ai.edumindaiapi.common.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDashboardResponse {
    private Widgets widgets;
    private List<AiInsight> aiInsights;
    private List<StudentEntry> students;
    private List<SubmissionEntry> submissionQueue;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Widgets {
        private long totalStudents;
        private long totalCourses;
        private long assignmentsPendingReview;
        private String averageGrade;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiInsight {
        private long id;
        private String text;
        private String severity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentEntry {
        private String id;
        private String name;
        private String email;
        private String course;
        private String grade;
        private int progress;
        private String lastActive;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmissionEntry {
        private String id;
        private String studentName;
        private String assignmentTitle;
        private String courseName;
        private String date;
        private String status;
    }
}
