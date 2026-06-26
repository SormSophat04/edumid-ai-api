package com.ai.edumindaiapi.repository;

import com.ai.edumindaiapi.domain.Assignment;
import com.ai.edumindaiapi.common.enums.AssignmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByUserId(Long userId);
    Page<Assignment> findByUserId(Long userId, Pageable pageable);
    List<Assignment> findByCourseId(Long courseId);
    List<Assignment> findByStatus(AssignmentStatus status);
    Page<Assignment> findByStatus(AssignmentStatus status, Pageable pageable);
    long countByStatus(AssignmentStatus status);
    long countByUserIdAndStatus(Long userId, AssignmentStatus status);
    List<Assignment> findByUserIdAndCourseId(Long userId, Long courseId);
}
