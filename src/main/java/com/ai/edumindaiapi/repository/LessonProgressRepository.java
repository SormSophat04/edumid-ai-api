package com.ai.edumindaiapi.repository;

import com.ai.edumindaiapi.domain.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);
    List<LessonProgress> findByUserId(Long userId);
    long countByUserIdAndCompletedTrue(Long userId);
    long countByUserId(Long userId);
}
