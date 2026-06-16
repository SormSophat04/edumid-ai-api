package com.ai.edumindaiapi.service;

import com.ai.edumindaiapi.common.dto.DashboardStatsResponse;
import com.ai.edumindaiapi.common.dto.RecentActivityResponse;

public interface DashboardService {
    DashboardStatsResponse getStudentStats(Long userId);
    RecentActivityResponse getRecentActivity(Long userId);
}
