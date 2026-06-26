package com.ai.edumindaiapi.mapper;

import com.ai.edumindaiapi.common.dto.QuizResultResponse;
import com.ai.edumindaiapi.domain.QuizAttempt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    @Mapping(target = "results", ignore = true)
    @Mapping(target = "correctCount", ignore = true)
    @Mapping(target = "totalCount", ignore = true)
    QuizResultResponse toResult(QuizAttempt attempt);
}
