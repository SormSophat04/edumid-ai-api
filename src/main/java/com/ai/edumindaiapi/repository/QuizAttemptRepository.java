package com.ai.edumindaiapi.repository;

import com.ai.edumindaiapi.domain.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserId(Long userId);
    List<QuizAttempt> findByUserIdAndTopic(Long userId, String topic);
    long countByUserId(Long userId);
}
