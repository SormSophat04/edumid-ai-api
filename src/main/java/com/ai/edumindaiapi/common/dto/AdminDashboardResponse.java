package com.ai.edumindaiapi.common.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private Stats stats;
    private List<RoleDistEntry> roleDistribution;
    private List<ActivityLogEntry> activityLogs;
    private List<AiUsageEntry> aiUsageStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stats {
        private long totalActiveUsers;
        private int activeServers;
        private String systemUptime;
        private int cpuUsage;
        private int memoryUsage;
        private long aiRequestsToday;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleDistEntry {
        private String role;
        private long count;
        private String color;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityLogEntry {
        private long id;
        private String user;
        private String action;
        private String time;
        private String type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiUsageEntry {
        private String time;
        private int requests;
    }
}
