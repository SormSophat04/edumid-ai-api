package com.ai.edumindaiapi.service;

import com.ai.edumindaiapi.common.dto.QuizGenerateRequest;
import com.ai.edumindaiapi.common.dto.QuizResponse;
import com.ai.edumindaiapi.common.dto.QuizResultResponse;

public interface QuizService {
    QuizResponse generateQuiz(Long userId, QuizGenerateRequest request);
    QuizResultResponse submitQuiz(Long userId, Long attemptId, String answersJson);
}
