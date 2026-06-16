package com.ai.edumindaiapi.service;

import com.ai.edumindaiapi.common.dto.AnalyticsResponse;

public interface AnalyticsService {
    AnalyticsResponse getAnalytics(Long userId);
}
