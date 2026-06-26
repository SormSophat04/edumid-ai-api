package com.ai.edumindaiapi.service.impl;

import com.ai.edumindaiapi.common.dto.CreateCourseRequest;
import com.ai.edumindaiapi.common.dto.CourseResponse;
import com.ai.edumindaiapi.common.exception.BadRequestException;
import com.ai.edumindaiapi.common.exception.ResourceNotFoundException;
import com.ai.edumindaiapi.domain.Course;
import com.ai.edumindaiapi.mapper.CourseMapper;
import com.ai.edumindaiapi.repository.CourseRepository;
import com.ai.edumindaiapi.repository.EnrollmentRepository;
import com.ai.edumindaiapi.repository.LessonProgressRepository;
import com.ai.edumindaiapi.repository.LessonRepository;
import com.ai.edumindaiapi.repository.ModuleRepository;
import com.ai.edumindaiapi.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private ModuleRepository moduleRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private LessonProgressRepository lessonProgressRepository;
    @Mock
    private CourseMapper courseMapper;
    @Mock
    private EnrollmentService enrollmentService;

    private CourseServiceImpl courseService;

    @BeforeEach
    void setUp() {
        courseService = new CourseServiceImpl(
                courseRepository, enrollmentRepository, moduleRepository,
                lessonRepository, lessonProgressRepository, courseMapper,
                enrollmentService);
    }

    @Test
    void createCourse_ShouldSaveAndReturnCourseResponse() {
        Long teacherId = 1L;
        CreateCourseRequest request = new CreateCourseRequest(
                "Java Basics", "Learn Java", "CS", "Easy", null);
        Course unsaved = Course.builder().title("Java Basics").teacherId(teacherId).build();
        Course saved = Course.builder().id(10L).title("Java Basics").teacherId(teacherId).build();
        CourseResponse expectedResponse = CourseResponse.builder().id(10L).title("Java Basics").build();

        when(courseMapper.toEntity(request, teacherId)).thenReturn(unsaved);
        when(courseRepository.save(unsaved)).thenReturn(saved);
        when(courseMapper.toResponse(saved)).thenReturn(expectedResponse);

        CourseResponse result = courseService.createCourse(teacherId, request);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Java Basics", result.getTitle());
        verify(courseRepository).save(unsaved);
    }

    @Test
    void enroll_ShouldEnrollSuccessfully() {
        Long courseId = 1L;
        Long userId = 5L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(new Course()));
        when(enrollmentService.isEnrolled(userId, courseId)).thenReturn(false);

        courseService.enroll(courseId, userId);

        verify(enrollmentService).enroll(userId, courseId);
    }

    @Test
    void enroll_ShouldThrowWhenCourseNotFound() {
        Long courseId = 999L;
        Long userId = 5L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> courseService.enroll(courseId, userId));
        verifyNoInteractions(enrollmentService);
    }

    @Test
    void enroll_ShouldThrowWhenAlreadyEnrolled() {
        Long courseId = 1L;
        Long userId = 5L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(new Course()));
        when(enrollmentService.isEnrolled(userId, courseId)).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> courseService.enroll(courseId, userId));
        verify(enrollmentService, never()).enroll(any(), any());
    }
}
