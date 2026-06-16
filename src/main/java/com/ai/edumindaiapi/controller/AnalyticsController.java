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

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/performance")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getAnalytics() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getAnalytics(userId)));
    }

    @GetMapping("/subject-strengths")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getSubjectStrengths() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getAnalytics(userId)));
    }

    @GetMapping("/study-hours")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getStudyHours() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getAnalytics(userId)));
    }

    @GetMapping("/grade-prediction")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getGradePrediction() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getAnalytics(userId)));
    }
}
