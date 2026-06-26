package com.ai.edumindaiapi.repository;

import com.ai.edumindaiapi.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTeacherId(Long teacherId);
}
