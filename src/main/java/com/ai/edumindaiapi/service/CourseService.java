package com.ai.edumindaiapi.service;

import com.ai.edumindaiapi.common.dto.CourseResponse;
import com.ai.edumindaiapi.common.dto.CourseSummaryResponse;
import java.util.List;

public interface CourseService {
    List<CourseSummaryResponse> getCourses(Long userId);
    CourseResponse getCourseDetail(Long courseId, Long userId);
    void updateProgress(Long courseId, Long userId, int progress);
    void completeLesson(Long courseId, Long lessonId, Long userId);
}
