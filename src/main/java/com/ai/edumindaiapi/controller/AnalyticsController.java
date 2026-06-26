package com.ai.edumindaiapi.controller;

import com.ai.edumindaiapi.common.dto.*;
import com.ai.edumindaiapi.security.AuthUser;
import com.ai.edumindaiapi.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((AuthUser) authentication.getPrincipal()).getId();
    }

    @GetMapping("/performance")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<AnalyticsResponse.PerformanceEntry>>> getPerformanceHistory() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getPerformanceHistory(getCurrentUserId())));
    }

    @GetMapping("/subject-strengths")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<AnalyticsResponse.SubjectStrength>>> getSubjectStrengths() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getSubjectStrengths(getCurrentUserId())));
    }

    @GetMapping("/study-hours")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<AnalyticsResponse.StudyHourEntry>>> getStudyHours() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getStudyHours(getCurrentUserId())));
    }

    @GetMapping("/grade-prediction")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AnalyticsResponse.GradePrediction>> getGradePrediction() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getGradePrediction(getCurrentUserId())));
    }
}
