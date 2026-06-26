package com.ai.edumindaiapi.service;

import com.ai.edumindaiapi.common.dto.AnalyticsResponse;
import java.util.List;

public interface AnalyticsService {
    AnalyticsResponse getAnalytics(Long userId);
    List<AnalyticsResponse.PerformanceEntry> getPerformanceHistory(Long userId);
    List<AnalyticsResponse.SubjectStrength> getSubjectStrengths(Long userId);
    List<AnalyticsResponse.StudyHourEntry> getStudyHours(Long userId);
    AnalyticsResponse.GradePrediction getGradePrediction(Long userId);
}
