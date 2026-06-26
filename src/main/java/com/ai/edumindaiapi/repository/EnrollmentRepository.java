package com.ai.edumindaiapi.repository;

import com.ai.edumindaiapi.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUserId(Long userId);
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
    long countByCourseId(Long courseId);
    long countByUserId(Long userId);
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}
