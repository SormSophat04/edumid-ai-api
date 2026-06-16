package com.ai.edumindaiapi.repository;

import com.ai.edumindaiapi.domain.Assignment;
import com.ai.edumindaiapi.common.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByUserId(Long userId);
    List<Assignment> findByCourseId(Long courseId);
    List<Assignment> findByStatus(AssignmentStatus status);
    long countByStatus(AssignmentStatus status);
    List<Assignment> findByUserIdAndCourseId(Long userId, Long courseId);
}
