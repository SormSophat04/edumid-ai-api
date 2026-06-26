package com.ai.edumindaiapi.mapper;

import com.ai.edumindaiapi.common.dto.CreateCourseRequest;
import com.ai.edumindaiapi.common.dto.CourseResponse;
import com.ai.edumindaiapi.common.dto.CourseSummaryResponse;
import com.ai.edumindaiapi.domain.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "description", source = "description")
    CourseSummaryResponse toSummary(Course course);

    @Mapping(target = "modules", ignore = true)
    @Mapping(target = "progress", ignore = true)
    CourseResponse toResponse(Course course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacherId", source = "teacherId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Course toEntity(CreateCourseRequest request, Long teacherId);
}
