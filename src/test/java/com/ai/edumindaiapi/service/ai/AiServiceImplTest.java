package com.ai.edumindaiapi.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AiServiceImplTest {

    private AiServiceImpl aiService;

    @BeforeEach
    void setUp() {
        aiService = new AiServiceImpl(new ObjectMapper());
        ReflectionTestUtils.setField(aiService, "apiKey", "");
        ReflectionTestUtils.setField(aiService, "apiUrl", "https://api.openai.com/v1/chat/completions");
        ReflectionTestUtils.setField(aiService, "model", "gpt-4");
    }

    @Test
    void mockQuizReturnsValidJson() {
        String quiz = aiService.generateQuiz("Java", "medium", 5);
        assertNotNull(quiz);
        assertTrue(quiz.startsWith("["));
        assertTrue(quiz.endsWith("]"));
        assertTrue(quiz.contains("\"question\""));
        assertTrue(quiz.contains("\"options\""));
        assertTrue(quiz.contains("\"answer\""));
    }

    @Test
    void mockTutorReplyReturnsResponse() {
        String reply = aiService.tutorReply("Java OOP", List.of(), "What is polymorphism?");
        assertNotNull(reply);
        assertFalse(reply.isEmpty());
    }

    @Test
    void mockGradingReturnsCorrectStructure() {
        Map<String, Object> result = aiService.gradeAssignment("Student code here");
        assertNotNull(result);
        assertTrue(result.containsKey("grammar"));
        assertTrue(result.containsKey("logic"));
        assertTrue(result.containsKey("completeness"));
        assertTrue(result.containsKey("text"));
    }

    @Test
    void mockPredictionReturnsCorrectStructure() {
        Map<String, Object> result = aiService.predictGrade(Map.of("score", 85));
        assertNotNull(result);
        assertTrue(result.containsKey("grade"));
        assertTrue(result.containsKey("confidence"));
        assertTrue(result.containsKey("insights"));
    }

    @Test
    void mockRecommendationReturnsString() {
        String recommendation = aiService.recommendPath(Map.of("topic", "Java"));
        assertNotNull(recommendation);
        assertFalse(recommendation.isEmpty());
    }

    @Test
    void mockInsightsReturnsList() {
        List<Map<String, String>> insights = aiService.generateInsights(Map.of());
        assertNotNull(insights);
        assertFalse(insights.isEmpty());
        Map<String, String> first = insights.get(0);
        assertTrue(first.containsKey("id"));
        assertTrue(first.containsKey("text"));
        assertTrue(first.containsKey("severity"));
    }

    @Test
    void emptyKeyUsesMockMode() {
        assertDoesNotThrow(() -> aiService.generateQuiz("Java", "easy", 3));
        assertDoesNotThrow(() -> aiService.tutorReply("test", List.of(), "hello"));
    }
}
