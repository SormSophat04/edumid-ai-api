package com.ai.edumindaiapi.service;

import com.ai.edumindaiapi.common.dto.LearningPathResponse;

public interface LearningPathService {
    LearningPathResponse getLearningPath(Long userId);
    LearningPathResponse.Recommendation getRecommendation(Long userId);
}
