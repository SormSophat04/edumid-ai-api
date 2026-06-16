package com.ai.edumindaiapi.service.impl;

import com.ai.edumindaiapi.common.dto.DashboardStatsResponse;
import com.ai.edumindaiapi.common.dto.RecentActivityResponse;
import com.ai.edumindaiapi.common.enums.AssignmentStatus;
import com.ai.edumindaiapi.domain.ActivityLog;
import com.ai.edumindaiapi.repository.ActivityLogRepository;
import com.ai.edumindaiapi.repository.AssignmentRepository;
import com.ai.edumindaiapi.repository.EnrollmentRepository;
import com.ai.edumindaiapi.repository.LessonProgressRepository;
import com.ai.edumindaiapi.repository.QuizAttemptRepository;
import com.ai.edumindaiapi.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final ActivityLogRepository activityLogRepository;

    @Override
    public DashboardStatsResponse getStudentStats(Long userId) {
        long coursesEnrolled = enrollmentRepository.countByUserId(userId);
        long assignmentsPending = assignmentRepository.countByStatus(AssignmentStatus.PENDING);
        long totalQuizAttempts = quizAttemptRepository.countByUserId(userId);

        double currentGpa = 0.0;
        if (totalQuizAttempts > 0) {
            currentGpa = Math.min(4.0, totalQuizAttempts * 0.3 + 2.0);
        }

        long completedLessons = lessonProgressRepository.countByUserIdAndCompletedTrue(userId);
        long learningStreak = completedLessons > 0 ? Math.min(99, completedLessons / 2 + 1) : 0;

        return DashboardStatsResponse.builder()
                .coursesEnrolled((int) coursesEnrolled)
                .assignmentsPending((int) assignmentsPending)
                .currentGpa(Math.round(currentGpa * 10.0) / 10.0)
                .learningStreak((int) learningStreak)
                .build();
    }

    @Override
    public RecentActivityResponse getRecentActivity(Long userId) {
        List<ActivityLog> logs = activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 10));

        List<RecentActivityResponse.ActivityEntry> entries = logs.stream().map(log -> {
            String timeAgo = formatTimeAgo(log.getCreatedAt());
            return RecentActivityResponse.ActivityEntry.builder()
                    .id(log.getId().intValue())
                    .type(log.getType())
                    .text(log.getText())
                    .time(timeAgo)
                    .icon(log.getIcon())
                    .build();
        }).toList();

        return RecentActivityResponse.builder()
                .recentActivity(entries)
                .build();
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
