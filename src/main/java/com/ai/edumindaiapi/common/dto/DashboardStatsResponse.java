package com.ai.edumindaiapi.common.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private int coursesEnrolled;
    private int assignmentsPending;
    private double currentGpa;
    private int learningStreak;
}
