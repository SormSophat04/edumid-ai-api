package com.ai.edumindaiapi.service.impl;

import com.ai.edumindaiapi.common.dto.AssignmentResponse;
import com.ai.edumindaiapi.common.dto.PageUtil;
import com.ai.edumindaiapi.common.dto.PagedResponse;
import com.ai.edumindaiapi.common.enums.AssignmentStatus;
import com.ai.edumindaiapi.domain.Assignment;
import com.ai.edumindaiapi.domain.Course;
import com.ai.edumindaiapi.mapper.AssignmentMapper;
import com.ai.edumindaiapi.repository.AssignmentRepository;
import com.ai.edumindaiapi.repository.CourseRepository;
import com.ai.edumindaiapi.service.AssignmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final AssignmentMapper assignmentMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<AssignmentResponse> getAssignments(Long userId) {
        List<Assignment> assignments = assignmentRepository.findByUserId(userId);
        return assignments.stream()
                .map(this::toResponseWithCourseName)
                .toList();
    }

    @Override
    public PagedResponse<AssignmentResponse> getAssignments(Long userId, Pageable pageable) {
        Page<Assignment> page = assignmentRepository.findByUserId(userId, pageable);
        Page<AssignmentResponse> mappedPage = page.map(this::toResponseWithCourseName);
        return PageUtil.from(mappedPage);
    }

    @Override
    public AssignmentResponse getAssignmentDetail(Long id) {
        Assignment assignment = assignmentRepository.findById(id).orElse(null);
        if (assignment == null) return null;
        return toResponseWithCourseName(assignment);
    }

    @Override
    @Transactional
    public AssignmentResponse submitAssignment(Long userId, Long id, String fileUrl) {
        Assignment assignment = assignmentRepository.findById(id).orElse(null);
        if (assignment == null) return null;
        assignment.setFileUrl(fileUrl);
        assignment.setStatus(AssignmentStatus.SUBMITTED);
        assignment = assignmentRepository.save(assignment);
        return toResponseWithCourseName(assignment);
    }

    @Override
    public List<AssignmentResponse> getPendingSubmissions() {
        List<Assignment> pending = assignmentRepository.findByStatus(AssignmentStatus.SUBMITTED);
        return pending.stream()
                .map(this::toResponseWithCourseName)
                .toList();
    }

    @Override
    @Transactional
    public AssignmentResponse gradeAssignment(Long id, int score, String feedbackJson) {
        Assignment assignment = assignmentRepository.findById(id).orElse(null);
        if (assignment == null) return null;
        assignment.setScore(score);
        assignment.setFeedbackJson(feedbackJson);
        assignment.setStatus(AssignmentStatus.GRADED);
        assignment = assignmentRepository.save(assignment);
        return toResponseWithCourseName(assignment);
    }

    private AssignmentResponse toResponseWithCourseName(Assignment assignment) {
        AssignmentResponse response = assignmentMapper.toResponse(assignment);
        response.setStatus(assignment.getStatus().name());
        response.setDueDate(assignment.getDueDate() != null ? assignment.getDueDate().toString() : null);

        Course course = courseRepository.findById(assignment.getCourseId()).orElse(null);
        response.setCourseName(course != null ? course.getTitle() : "Unknown Course");

        if (assignment.getFeedbackJson() != null && !assignment.getFeedbackJson().isEmpty()) {
            try {
                Map<String, Object> feedbackMap = objectMapper.readValue(assignment.getFeedbackJson(),
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                AssignmentResponse.FeedbackDto feedback = AssignmentResponse.FeedbackDto.builder()
                        .grammar(feedbackMap.containsKey("grammar") ? ((Number) feedbackMap.get("grammar")).intValue() : 0)
                        .logic(feedbackMap.containsKey("logic") ? ((Number) feedbackMap.get("logic")).intValue() : 0)
                        .completeness(feedbackMap.containsKey("completeness") ? ((Number) feedbackMap.get("completeness")).intValue() : 0)
                        .text((String) feedbackMap.get("text"))
                        .build();
                response.setFeedback(feedback);
            } catch (JsonProcessingException ignored) {
            }
        }

        return response;
    }
}
