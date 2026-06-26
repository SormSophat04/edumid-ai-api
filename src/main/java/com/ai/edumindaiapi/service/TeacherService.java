package com.ai.edumindaiapi.service;

import com.ai.edumindaiapi.common.dto.TeacherDashboardResponse;
import java.util.List;

public interface TeacherService {
    TeacherDashboardResponse getDashboard(Long teacherId);
    List<TeacherDashboardResponse.StudentEntry> getStudents();
    List<TeacherDashboardResponse.AiInsight> getInsights();
    List<TeacherDashboardResponse.SubmissionEntry> getPendingSubmissions();
}
