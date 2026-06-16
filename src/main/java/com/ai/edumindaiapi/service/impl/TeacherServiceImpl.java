package com.ai.edumindaiapi.service.impl;

import com.ai.edumindaiapi.common.dto.TeacherDashboardResponse;
import com.ai.edumindaiapi.domain.Assignment;
import com.ai.edumindaiapi.domain.Course;
import com.ai.edumindaiapi.domain.Enrollment;
import com.ai.edumindaiapi.domain.User;
import com.ai.edumindaiapi.repository.AssignmentRepository;
import com.ai.edumindaiapi.repository.CourseRepository;
import com.ai.edumindaiapi.repository.EnrollmentRepository;
import com.ai.edumindaiapi.repository.UserRepository;
import com.ai.edumindaiapi.service.TeacherService;
import com.ai.edumindaiapi.service.ai.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final AiService aiService;

    @Override
    public TeacherDashboardResponse getDashboard(Long teacherId) {
        List<Course> teacherCourses = courseRepository.findByTeacherId(teacherId);
        List<Long> courseIds = teacherCourses.stream().map(Course::getId).toList();

        long totalStudents = 0;
        for (Long cid : courseIds) {
            totalStudents += enrollmentRepository.countByCourseId(cid);
        }

        long assignmentsPendingReview = assignmentRepository.countByStatus(
                com.ai.edumindaiapi.common.enums.AssignmentStatus.SUBMITTED);

        TeacherDashboardResponse.Widgets widgets = TeacherDashboardResponse.Widgets.builder()
                .totalStudents((int) totalStudents)
                .totalCourses(teacherCourses.size())
                .assignmentsPendingReview((int) assignmentsPendingReview)
                .averageGrade("B+ (84.2%)")
                .build();

        List<TeacherDashboardResponse.StudentEntry> students = getStudentsForTeacher(courseIds);
        List<TeacherDashboardResponse.SubmissionEntry> submissions = getPendingSubmissionsForTeacher(courseIds);
        List<TeacherDashboardResponse.AiInsight> insights = getInsights();

        return TeacherDashboardResponse.builder()
                .widgets(widgets)
                .aiInsights(insights)
                .students(students)
                .submissionQueue(submissions)
                .build();
    }

    @Override
    public List<TeacherDashboardResponse.StudentEntry> getStudents() {
        return getStudentsForTeacher(List.of());
    }

    @Override
    public List<TeacherDashboardResponse.AiInsight> getInsights() {
        return aiService.generateInsights(Map.of()).stream()
                .map(i -> TeacherDashboardResponse.AiInsight.builder()
                        .id(Long.parseLong(i.get("id")))
                        .text(i.get("text"))
                        .severity(i.get("severity"))
                        .build()).toList();
    }

    @Override
    public List<TeacherDashboardResponse.SubmissionEntry> getPendingSubmissions() {
        return getPendingSubmissionsForTeacher(List.of());
    }

    private List<TeacherDashboardResponse.StudentEntry> getStudentsForTeacher(List<Long> courseIds) {
        List<Enrollment> enrollments;
        if (courseIds.isEmpty()) {
            enrollments = List.of();
        } else {
            enrollments = enrollmentRepository.findAll().stream()
                    .filter(e -> courseIds.contains(e.getCourseId()))
                    .toList();
        }

        Set<Long> userIds = enrollments.stream().map(Enrollment::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Map<Long, String> courseNameMap = courseIds.isEmpty() ? Map.of() :
                courseRepository.findAllById(courseIds).stream()
                        .collect(Collectors.toMap(Course::getId, Course::getTitle));

        return enrollments.stream().map(e -> {
            User user = userMap.get(e.getUserId());
            return TeacherDashboardResponse.StudentEntry.builder()
                    .id("st-" + e.getUserId())
                    .name(user != null ? user.getName() : "Unknown")
                    .email(user != null ? user.getEmail() : "")
                    .course(courseNameMap.getOrDefault(e.getCourseId(), "Unknown"))
                    .grade("B")
                    .progress(e.getProgress())
                    .lastActive("N/A")
                    .build();
        }).toList();
    }

    private List<TeacherDashboardResponse.SubmissionEntry> getPendingSubmissionsForTeacher(List<Long> courseIds) {
        List<Assignment> assignments;
        if (courseIds.isEmpty()) {
            assignments = assignmentRepository.findByStatus(com.ai.edumindaiapi.common.enums.AssignmentStatus.SUBMITTED);
        } else {
            assignments = assignmentRepository.findAll().stream()
                    .filter(a -> a.getStatus() == com.ai.edumindaiapi.common.enums.AssignmentStatus.SUBMITTED)
                    .filter(a -> courseIds.contains(a.getCourseId()))
                    .toList();
        }

        Map<Long, User> userMap = userRepository.findAll().stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Map<Long, String> courseNameMap = courseRepository.findAll().stream()
                .collect(Collectors.toMap(Course::getId, Course::getTitle));

        return assignments.stream().map(a -> {
            User user = userMap.get(a.getUserId());
            return TeacherDashboardResponse.SubmissionEntry.builder()
                    .id("sub-" + a.getId())
                    .studentName(user != null ? user.getName() : "Unknown")
                    .assignmentTitle(a.getTitle())
                    .courseName(courseNameMap.getOrDefault(a.getCourseId(), "Unknown"))
                    .date(a.getUpdatedAt() != null ? a.getUpdatedAt().toLocalDate().toString() : "N/A")
                    .status("Needs Review")
                    .build();
        }).toList();
    }
}
