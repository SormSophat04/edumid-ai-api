package com.ai.edumindaiapi.mapper;

import com.ai.edumindaiapi.common.dto.LearningPathResponse;
import com.ai.edumindaiapi.domain.LearningPathMilestone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LearningPathMapper {
    @Mapping(target = "status", expression = "java(milestone.getStatus().name().toLowerCase())")
    @Mapping(target = "time", ignore = true)
    LearningPathResponse.MilestoneDto toMilestone(LearningPathMilestone milestone);
}
