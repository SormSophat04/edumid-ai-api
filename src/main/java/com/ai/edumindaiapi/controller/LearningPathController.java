package com.ai.edumindaiapi.controller;

import com.ai.edumindaiapi.common.dto.ApiResponse;
import com.ai.edumindaiapi.common.dto.LearningPathResponse;
import com.ai.edumindaiapi.security.AuthUser;
import com.ai.edumindaiapi.service.LearningPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learning-path")
@RequiredArgsConstructor
public class LearningPathController {

    private final LearningPathService learningPathService;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<LearningPathResponse>> getLearningPath() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(learningPathService.getLearningPath(userId)));
    }

    @GetMapping("/recommendation")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<LearningPathResponse.Recommendation>> getRecommendation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(learningPathService.getRecommendation(userId)));
    }
}
