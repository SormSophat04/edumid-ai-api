package com.ai.edumindaiapi.service.ai;

import java.util.List;
import java.util.Map;

public interface AiService {
    String generateQuiz(String topic, String difficulty, int count);
    String tutorReply(String context, List<Map<String, String>> conversationHistory, String userMessage);
    Map<String, Object> gradeAssignment(String content);
    Map<String, Object> predictGrade(Map<String, Object> userPerformanceData);
    String recommendPath(Map<String, Object> userData);
    List<Map<String, String>> generateInsights(Map<String, Object> data);
}
