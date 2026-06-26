package com.ai.edumindaiapi.controller;

import com.ai.edumindaiapi.common.dto.*;
import com.ai.edumindaiapi.security.AuthUser;
import com.ai.edumindaiapi.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CourseSummaryResponse>>> getCourses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(courseService.getCourses(userId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseDetail(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(courseService.getCourseDetail(id, userId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long teacherId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(courseService.createCourse(teacherId, request)));
    }

    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> enroll(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        courseService.enroll(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Enrolled successfully", null));
    }

    @PutMapping("/{id}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> updateProgress(
            @PathVariable Long id,
            @RequestBody CourseProgressRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        courseService.updateProgress(id, userId, request.progress());
        return ResponseEntity.ok(ApiResponse.ok("Progress updated", null));
    }

    @PutMapping("/{courseId}/lessons/{lessonId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> completeLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        courseService.completeLesson(courseId, lessonId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Lesson completed", null));
    }
}
