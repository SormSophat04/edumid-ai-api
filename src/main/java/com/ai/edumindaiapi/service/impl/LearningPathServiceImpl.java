package com.ai.edumindaiapi.service.impl;

import com.ai.edumindaiapi.common.dto.LearningPathResponse;
import com.ai.edumindaiapi.domain.LearningPathMilestone;
import com.ai.edumindaiapi.mapper.LearningPathMapper;
import com.ai.edumindaiapi.repository.LearningPathMilestoneRepository;
import com.ai.edumindaiapi.service.LearningPathService;
import com.ai.edumindaiapi.service.ai.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LearningPathServiceImpl implements LearningPathService {

    private final LearningPathMilestoneRepository milestoneRepository;
    private final LearningPathMapper learningPathMapper;
    private final AiService aiService;

    @Override
    public LearningPathResponse getLearningPath(Long userId) {
        List<LearningPathMilestone> milestones = milestoneRepository.findByUserIdOrderByOrderIndex(userId);

        List<LearningPathResponse.MilestoneDto> milestoneDtos = milestones.stream()
                .map(m -> {
                    LearningPathResponse.MilestoneDto dto = learningPathMapper.toMilestone(m);
                    dto.setCompletion(m.getCompletion());
                    dto.setDifficulty(m.getDifficulty());
                    return dto;
                })
                .toList();

        LearningPathResponse.Recommendation recommendation = getRecommendation(userId);

        return LearningPathResponse.builder()
                .milestones(milestoneDtos)
                .recommendation(recommendation)
                .build();
    }

    @Override
    public LearningPathResponse.Recommendation getRecommendation(Long userId) {
        List<LearningPathMilestone> milestones = milestoneRepository.findByUserIdOrderByOrderIndex(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("milestones", milestones.stream()
                .map(m -> Map.of("title", m.getTitle(), "status", m.getStatus().name(), "completion", m.getCompletion()))
                .toList());

        String aiRecommendation = aiService.recommendPath(userData);

        return LearningPathResponse.Recommendation.builder()
                .title("Next milestone priority")
                .description(aiRecommendation)
                .build();
    }
}
