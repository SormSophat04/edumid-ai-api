package com.ai.edumindaiapi.repository;

import com.ai.edumindaiapi.domain.LearningPathMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LearningPathMilestoneRepository extends JpaRepository<LearningPathMilestone, Long> {
    List<LearningPathMilestone> findByUserIdOrderByOrderIndex(Long userId);
}
