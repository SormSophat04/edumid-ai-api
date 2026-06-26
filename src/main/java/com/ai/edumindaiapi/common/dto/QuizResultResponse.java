package com.ai.edumindaiapi.common.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultResponse {
    private Long attemptId;
    private int score;
    private int correctCount;
    private int totalCount;
    private List<QuestionResultDto> results;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResultDto {
        private int questionId;
        private String question;
        private List<String> options;
        private int correctAnswer;
        private int yourAnswer;
        private boolean correct;
        private String explanation;
    }
}
