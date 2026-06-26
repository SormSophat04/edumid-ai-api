package com.ai.edumindaiapi.service.impl;

import com.ai.edumindaiapi.common.dto.CreateCourseRequest;
import com.ai.edumindaiapi.common.dto.CourseResponse;
import com.ai.edumindaiapi.common.dto.CourseSummaryResponse;
import com.ai.edumindaiapi.common.exception.BadRequestException;
import com.ai.edumindaiapi.common.exception.ResourceNotFoundException;
import com.ai.edumindaiapi.domain.Course;
import com.ai.edumindaiapi.domain.Enrollment;
import com.ai.edumindaiapi.domain.Lesson;
import com.ai.edumindaiapi.domain.LessonProgress;
import com.ai.edumindaiapi.domain.Module;
import com.ai.edumindaiapi.mapper.CourseMapper;
import com.ai.edumindaiapi.repository.CourseRepository;
import com.ai.edumindaiapi.repository.EnrollmentRepository;
import com.ai.edumindaiapi.repository.LessonProgressRepository;
import com.ai.edumindaiapi.repository.LessonRepository;
import com.ai.edumindaiapi.repository.ModuleRepository;
import com.ai.edumindaiapi.service.CourseService;
import com.ai.edumindaiapi.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseMapper courseMapper;
    private final EnrollmentService enrollmentService;

    @Override
    public List<CourseSummaryResponse> getCourses(Long userId) {
        List<Course> courses = courseRepository.findAll();
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        Map<Long, Integer> progressMap = enrollments.stream()
                .collect(Collectors.toMap(Enrollment::getCourseId, Enrollment::getProgress));

        return courses.stream().map(course -> {
            CourseSummaryResponse summary = courseMapper.toSummary(course);
            summary.setProgress(progressMap.getOrDefault(course.getId(), 0));
            return summary;
        }).toList();
    }

    @Override
    public CourseResponse getCourseDetail(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElse(null);
        int progress = enrollment != null ? enrollment.getProgress() : 0;

        List<Module> modules = moduleRepository.findByCourseIdOrderByOrderIndex(course.getId());

        List<LessonProgress> userProgress = lessonProgressRepository.findByUserId(userId);
        Set<Long> completedLessonIds = userProgress.stream()
                .filter(LessonProgress::getCompleted)
                .map(LessonProgress::getLessonId)
                .collect(Collectors.toSet());

        List<Long> moduleIds = modules.stream().map(Module::getId).toList();
        List<Lesson> allLessons = lessonRepository.findByModuleIdInOrderByOrderIndex(moduleIds);
        Map<Long, List<Lesson>> lessonsByModule = allLessons.stream()
                .collect(Collectors.groupingBy(Lesson::getModuleId));

        List<CourseResponse.ModuleDto> moduleDtos = modules.stream().map(module -> {
            List<Lesson> moduleLessons = lessonsByModule.getOrDefault(module.getId(), List.of());

            List<CourseResponse.LessonDto> lessonDtos = moduleLessons.stream().map(lesson -> {
                boolean completed = completedLessonIds.contains(lesson.getId());
                return CourseResponse.LessonDto.builder()
                        .id(lesson.getId())
                        .title(lesson.getTitle())
                        .type(lesson.getType().name().toLowerCase())
                        .duration(lesson.getDuration())
                        .videoUrl(lesson.getVideoUrl())
                        .content(lesson.getContent())
                        .orderIndex(lesson.getOrderIndex())
                        .completed(completed)
                        .build();
            }).toList();

            boolean moduleCompleted = !moduleLessons.isEmpty() && lessonDtos.stream().allMatch(CourseResponse.LessonDto::isCompleted);
            return CourseResponse.ModuleDto.builder()
                    .id(module.getId())
                    .title(module.getTitle())
                    .orderIndex(module.getOrderIndex())
                    .completed(moduleCompleted)
                    .lessons(lessonDtos)
                    .build();
        }).toList();

        CourseResponse response = courseMapper.toResponse(course);
        response.setProgress(progress);
        response.setModules(moduleDtos);
        return response;
    }

    @Override
    @Transactional
    public CourseResponse createCourse(Long teacherId, CreateCourseRequest request) {
        Course course = courseMapper.toEntity(request, teacherId);
        course = courseRepository.save(course);
        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional
    public void enroll(Long courseId, Long userId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
        if (enrollmentService.isEnrolled(userId, courseId)) {
            throw new BadRequestException("Already enrolled in this course");
        }
        enrollmentService.enroll(userId, courseId);
    }

    @Override
    @Transactional
    public void updateProgress(Long courseId, Long userId, int progress) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElse(Enrollment.builder().userId(userId).courseId(courseId).build());
        enrollment.setProgress(progress);
        enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional
    public void completeLesson(Long courseId, Long lessonId, Long userId) {
        LessonProgress existing = lessonProgressRepository.findByUserIdAndLessonId(userId, lessonId)
                .orElse(null);
        if (existing == null) {
            LessonProgress progress = LessonProgress.builder()
                    .userId(userId)
                    .lessonId(lessonId)
                    .completed(true)
                    .completedAt(LocalDateTime.now())
                    .build();
            lessonProgressRepository.save(progress);
        } else if (!existing.getCompleted()) {
            existing.setCompleted(true);
            existing.setCompletedAt(LocalDateTime.now());
            lessonProgressRepository.save(existing);
        }
    }
}
