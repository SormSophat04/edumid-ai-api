package com.ai.edumindaiapi.service;

import com.ai.edumindaiapi.common.dto.AssignmentResponse;
import java.util.List;

public interface AssignmentService {
    List<AssignmentResponse> getAssignments(Long userId);
    AssignmentResponse getAssignmentDetail(Long id);
    AssignmentResponse submitAssignment(Long userId, Long id, String fileUrl);
    List<AssignmentResponse> getPendingSubmissions();
    AssignmentResponse gradeAssignment(Long id, int score, String feedbackJson);
}
