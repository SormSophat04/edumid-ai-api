package com.ai.edumindaiapi.controller;

import com.ai.edumindaiapi.common.dto.*;
import com.ai.edumindaiapi.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getDashboard()));
    }

    @GetMapping("/users/role-distribution")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminDashboardResponse.RoleDistEntry>>> getRoleDistribution() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getRoleDistribution()));
    }

    @GetMapping("/activity-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminDashboardResponse.ActivityLogEntry>>> getActivityLogs() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getActivityLogs()));
    }

    @GetMapping("/ai-usage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminDashboardResponse.AiUsageEntry>>> getAiUsage() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getAiUsage()));
    }
}
