package com.ai.edumindaiapi.controller;

import com.ai.edumindaiapi.common.dto.*;
import com.ai.edumindaiapi.security.AuthUser;
import com.ai.edumindaiapi.service.AssignmentService;
import com.ai.edumindaiapi.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final AssignmentService assignmentService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherDashboardResponse>> getDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long teacherId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(teacherService.getDashboard(teacherId)));
    }

    @GetMapping("/students")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<TeacherDashboardResponse.StudentEntry>>> getStudents() {
        return ResponseEntity.ok(ApiResponse.ok(teacherService.getStudents()));
    }

    @GetMapping("/insights")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<TeacherDashboardResponse.AiInsight>>> getInsights() {
        return ResponseEntity.ok(ApiResponse.ok(teacherService.getInsights()));
    }

    @GetMapping("/submissions/pending")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<TeacherDashboardResponse.SubmissionEntry>>> getPendingSubmissions() {
        return ResponseEntity.ok(ApiResponse.ok(teacherService.getPendingSubmissions()));
    }

    @PostMapping("/assignments/{id}/grade")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> gradeAssignment(
            @PathVariable Long id,
            @RequestBody GradeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(assignmentService.gradeAssignment(id, request.score(), request.feedbackJson())));
    }
}
