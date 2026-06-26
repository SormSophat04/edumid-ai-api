package com.ai.edumindaiapi.service;

import com.ai.edumindaiapi.common.dto.AdminDashboardResponse;
import java.util.List;

public interface AdminService {
    AdminDashboardResponse getDashboard();
    List<AdminDashboardResponse.RoleDistEntry> getRoleDistribution();
    List<AdminDashboardResponse.ActivityLogEntry> getActivityLogs();
    List<AdminDashboardResponse.AiUsageEntry> getAiUsage();
}
