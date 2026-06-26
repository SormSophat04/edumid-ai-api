package com.ai.edumindaiapi.mapper;

import com.ai.edumindaiapi.common.dto.AssignmentResponse;
import com.ai.edumindaiapi.domain.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {
    @Mapping(target = "courseName", ignore = true)
    @Mapping(target = "feedback", ignore = true)
    AssignmentResponse toResponse(Assignment assignment);
}
