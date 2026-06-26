package com.ai.edumindaiapi.controller;

import com.ai.edumindaiapi.common.dto.ApiResponse;
import com.ai.edumindaiapi.common.dto.DashboardStatsResponse;
import com.ai.edumindaiapi.common.dto.RecentActivityResponse;
import com.ai.edumindaiapi.security.AuthUser;
import com.ai.edumindaiapi.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getStudentStats(userId)));
    }

    @GetMapping("/recent-activity")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<RecentActivityResponse>> getRecentActivity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getRecentActivity(userId)));
    }
}
