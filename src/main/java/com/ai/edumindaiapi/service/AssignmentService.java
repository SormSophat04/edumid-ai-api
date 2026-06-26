package com.ai.edumindaiapi.service;

import com.ai.edumindaiapi.common.dto.AssignmentResponse;
import com.ai.edumindaiapi.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssignmentService {
    List<AssignmentResponse> getAssignments(Long userId);
    PagedResponse<AssignmentResponse> getAssignments(Long userId, Pageable pageable);
    AssignmentResponse getAssignmentDetail(Long id);
    AssignmentResponse submitAssignment(Long userId, Long id, String fileUrl);
    List<AssignmentResponse> getPendingSubmissions();
    AssignmentResponse gradeAssignment(Long id, int score, String feedbackJson);
}
