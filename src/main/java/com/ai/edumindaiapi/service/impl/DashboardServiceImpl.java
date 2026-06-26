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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DashboardStatsResponse getStudentStats(Long userId) {
        long coursesEnrolled = enrollmentRepository.countByUserId(userId);
        long assignmentsPending = assignmentRepository.countByUserIdAndStatus(userId, AssignmentStatus.PENDING);
        long totalQuizAttempts = quizAttemptRepository.countByUserId(userId);

        double currentGpa = 0.0;
        if (totalQuizAttempts > 0) {
            currentGpa = Math.min(4.0, totalQuizAttempts * 0.3 + 2.0);
        }

        long learningStreak = calculateLearningStreak(userId);

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

    private long calculateLearningStreak(Long userId) {
        List<java.sql.Date> dates = entityManager.createNativeQuery(
        """
            SELECT DISTINCT activity_date FROM (
              SELECT DATE(created_at) AS activity_date FROM activity_logs WHERE user_id = :userId
              UNION
              SELECT DATE(completed_at) FROM lesson_progress WHERE user_id = :userId AND completed_at IS NOT NULL
              UNION
              SELECT DATE(created_at) FROM quiz_attempts WHERE user_id = :userId
              UNION
              SELECT DISTINCT DATE(cm.created_at) FROM chat_messages cm
                JOIN chat_conversations cc ON cm.conversation_id = cc.id
                WHERE cc.user_id = :userId
            ) all_dates ORDER BY activity_date DESC
            """)
            .setParameter("userId", userId)
            .getResultList();

        if (dates.isEmpty()) return 0;

        LocalDate today = LocalDate.now();
        LocalDate latestDate = dates.get(0).toLocalDate();

        if (ChronoUnit.DAYS.between(latestDate, today) > 1) return 0;

        long streak = 0;
        LocalDate expected = latestDate;

        for (java.sql.Date date : dates) {
            LocalDate activityDate = date.toLocalDate();
            if (activityDate.equals(expected)) {
                streak++;
                expected = expected.minusDays(1);
            } else if (activityDate.isBefore(expected)) {
                break;
            }
        }

        return streak;
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
