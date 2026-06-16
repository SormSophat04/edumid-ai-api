package com.ai.edumindaiapi.service;

import com.ai.edumindaiapi.domain.Enrollment;
import java.util.List;

public interface EnrollmentService {
    void enroll(Long userId, Long courseId);
    List<Enrollment> getEnrollments(Long userId);
    boolean isEnrolled(Long userId, Long courseId);
}
