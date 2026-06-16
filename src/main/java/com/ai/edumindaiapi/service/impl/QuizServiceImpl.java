package com.ai.edumindaiapi.service.impl;

import com.ai.edumindaiapi.common.dto.QuizGenerateRequest;
import com.ai.edumindaiapi.common.dto.QuizResponse;
import com.ai.edumindaiapi.common.dto.QuizResultResponse;
import com.ai.edumindaiapi.domain.QuizAttempt;
import com.ai.edumindaiapi.mapper.QuizMapper;
import com.ai.edumindaiapi.repository.QuizAttemptRepository;
import com.ai.edumindaiapi.service.QuizService;
import com.ai.edumindaiapi.service.ai.AiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final AiService aiService;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizMapper quizMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public QuizResponse generateQuiz(Long userId, QuizGenerateRequest request) {
        String questionsJson = aiService.generateQuiz(request.topic(), request.difficulty(), request.count());

        QuizAttempt attempt = QuizAttempt.builder()
                .userId(userId)
                .topic(request.topic())
                .difficulty(request.difficulty())
                .questionsJson(questionsJson)
                .build();
        attempt = quizAttemptRepository.save(attempt);

        try {
            List<Map<String, Object>> questions = objectMapper.readValue(questionsJson,
                    new TypeReference<List<Map<String, Object>>>() {});
            List<QuizResponse.QuestionDto> questionDtos = new ArrayList<>();
            for (Map<String, Object> q : questions) {
                questionDtos.add(QuizResponse.QuestionDto.builder()
                        .id((int) q.get("id"))
                        .question((String) q.get("question"))
                        .options((List<String>) q.get("options"))
                        .build());
            }
            return QuizResponse.builder()
                    .attemptId(attempt.getId())
                    .title(request.topic() + " Quiz")
                    .topic(request.topic())
                    .difficulty(request.difficulty())
                    .questions(questionDtos)
                    .build();
        } catch (JsonProcessingException e) {
            return QuizResponse.builder()
                    .attemptId(attempt.getId())
                    .title(request.topic() + " Quiz")
                    .topic(request.topic())
                    .difficulty(request.difficulty())
                    .questions(List.of(
                            QuizResponse.QuestionDto.builder().id(1).question("Sample question?").options(List.of("A", "B", "C", "D")).build()
                    )).build();
        }
    }

    @Override
    @Transactional
    public QuizResultResponse submitQuiz(Long userId, Long attemptId, String answersJson) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElse(null);
        if (attempt == null) {
            return QuizResultResponse.builder().attemptId(attemptId).score(0).correctCount(0).totalCount(0).build();
        }

        attempt.setAnswersJson(answersJson);
        attempt.setCompletedAt(LocalDateTime.now());

        try {
            List<Integer> answers = objectMapper.readValue(answersJson, new TypeReference<List<Integer>>() {});
            List<Map<String, Object>> questions = objectMapper.readValue(attempt.getQuestionsJson(),
                    new TypeReference<List<Map<String, Object>>>() {});

            int total = Math.min(answers.size(), questions.size());
            int correctCount = 0;
            List<QuizResultResponse.QuestionResultDto> results = new ArrayList<>();

            for (int i = 0; i < total; i++) {
                Map<String, Object> q = questions.get(i);
                int correctAnswer = (int) q.get("answer");
                int yourAnswer = answers.get(i);
                boolean isCorrect = yourAnswer == correctAnswer;
                if (isCorrect) correctCount++;

                results.add(QuizResultResponse.QuestionResultDto.builder()
                        .questionId(i + 1)
                        .question((String) q.get("question"))
                        .options((List<String>) q.get("options"))
                        .correctAnswer(correctAnswer)
                        .yourAnswer(yourAnswer)
                        .correct(isCorrect)
                        .explanation((String) q.get("explanation"))
                        .build());
            }

            int score = total > 0 ? (correctCount * 100 / total) : 0;
            attempt.setScore(score);
            quizAttemptRepository.save(attempt);

            return QuizResultResponse.builder()
                    .attemptId(attemptId)
                    .score(score)
                    .correctCount(correctCount)
                    .totalCount(total)
                    .results(results)
                    .build();

        } catch (JsonProcessingException e) {
            attempt.setScore(0);
            quizAttemptRepository.save(attempt);
            return QuizResultResponse.builder().attemptId(attemptId).score(0).correctCount(0).totalCount(0).build();
        }
    }
}
