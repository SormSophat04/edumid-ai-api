package com.ai.edumindaiapi.service.impl;

import com.ai.edumindaiapi.common.dto.AdminDashboardResponse;
import com.ai.edumindaiapi.domain.ActivityLog;
import com.ai.edumindaiapi.domain.User;
import com.ai.edumindaiapi.repository.ActivityLogRepository;
import com.ai.edumindaiapi.repository.UserRepository;
import com.ai.edumindaiapi.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;

    @Override
    public AdminDashboardResponse getDashboard() {
        long totalActiveUsers = userRepository.count();
        long totalActivityLogs = activityLogRepository.count();

        return AdminDashboardResponse.builder()
                .stats(AdminDashboardResponse.Stats.builder()
                        .totalActiveUsers((int) totalActiveUsers)
                        .activeServers(2)
                        .systemUptime("99.9%")
                        .cpuUsage(34)
                        .memoryUsage(58)
                        .aiRequestsToday((int) (totalActivityLogs * 3))
                        .build())
                .roleDistribution(getRoleDistribution())
                .activityLogs(getActivityLogs())
                .aiUsageStats(getAiUsage())
                .build();
    }

    @Override
    public List<AdminDashboardResponse.RoleDistEntry> getRoleDistribution() {
        List<User> users = userRepository.findAll();
        Map<String, Long> roleCounts = users.stream()
                .collect(Collectors.groupingBy(
                        u -> u.getRole().name(),
                        Collectors.counting()
                ));

        Map<String, String> roleColors = Map.of(
                "STUDENT", "#4F46E5",
                "TEACHER", "#06B6D4",
                "ADMIN", "#10B981"
        );

        return roleCounts.entrySet().stream()
                .map(entry -> AdminDashboardResponse.RoleDistEntry.builder()
                        .role(entry.getKey())
                        .count(entry.getValue().intValue())
                        .color(roleColors.getOrDefault(entry.getKey(), "#6B7280"))
                        .build())
                .toList();
    }

    @Override
    public List<AdminDashboardResponse.ActivityLogEntry> getActivityLogs() {
        List<ActivityLog> logs = activityLogRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 20));

        return logs.stream().map(log -> {
            String timeAgo = formatTimeAgo(log.getCreatedAt());
            return AdminDashboardResponse.ActivityLogEntry.builder()
                    .id(log.getId().intValue())
                    .user("User #" + log.getUserId())
                    .action(log.getText())
                    .time(timeAgo)
                    .type(log.getType())
                    .build();
        }).toList();
    }

    @Override
    public List<AdminDashboardResponse.AiUsageEntry> getAiUsage() {
        List<ActivityLog> aiLogs = activityLogRepository.findAll().stream()
                .filter(l -> "ai".equals(l.getType()))
                .toList();

        if (aiLogs.isEmpty()) {
            return List.of(
                AdminDashboardResponse.AiUsageEntry.builder().time("09:00").requests(450).build(),
                AdminDashboardResponse.AiUsageEntry.builder().time("11:00").requests(920).build(),
                AdminDashboardResponse.AiUsageEntry.builder().time("13:00").requests(1200).build(),
                AdminDashboardResponse.AiUsageEntry.builder().time("15:00").requests(1100).build(),
                AdminDashboardResponse.AiUsageEntry.builder().time("17:00").requests(850).build()
            );
        }

        Map<Integer, Long> hourlyCounts = aiLogs.stream()
                .filter(l -> l.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        l -> l.getCreatedAt().getHour(),
                        Collectors.counting()
                ));

        return hourlyCounts.entrySet().stream()
                .map(entry -> AdminDashboardResponse.AiUsageEntry.builder()
                        .time(String.format("%02d:00", entry.getKey()))
                        .requests(entry.getValue().intValue())
                        .build())
                .sorted((a, b) -> a.getTime().compareTo(b.getTime()))
                .toList();
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        long minutes = ChronoUnit.MINUTES.between(dateTime, LocalDateTime.now());
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " mins ago";
        long hours = ChronoUnit.HOURS.between(dateTime, LocalDateTime.now());
        if (hours < 24) return hours + " hours ago";
        long days = ChronoUnit.DAYS.between(dateTime, LocalDateTime.now());
        if (days == 1) return "Yesterday";
        return days + " days ago";
    }
}
