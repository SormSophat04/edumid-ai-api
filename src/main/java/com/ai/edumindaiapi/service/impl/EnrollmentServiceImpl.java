package com.ai.edumindaiapi.service.impl;

import com.ai.edumindaiapi.domain.Enrollment;
import com.ai.edumindaiapi.repository.EnrollmentRepository;
import com.ai.edumindaiapi.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void enroll(Long userId, Long courseId) {
        if (!enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            Enrollment enrollment = Enrollment.builder()
                .userId(userId)
                .courseId(courseId)
                .progress(0)
                .build();
            enrollmentRepository.save(enrollment);
        }
    }

    @Override
    public List<Enrollment> getEnrollments(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    @Override
    public boolean isEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }
}
