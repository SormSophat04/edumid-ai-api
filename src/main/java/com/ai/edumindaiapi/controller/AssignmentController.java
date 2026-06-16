package com.ai.edumindaiapi.controller;

import com.ai.edumindaiapi.common.dto.*;
import com.ai.edumindaiapi.security.AuthUser;
import com.ai.edumindaiapi.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAssignments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(assignmentService.getAssignments(userId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> getAssignmentDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(assignmentService.getAssignmentDetail(id)));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> submitAssignment(
            @PathVariable Long id,
            @RequestParam("file") String fileUrl) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(assignmentService.submitAssignment(userId, id, fileUrl)));
    }

    @GetMapping("/{id}/feedback")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> getFeedback(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(assignmentService.getAssignmentDetail(id)));
    }
}
