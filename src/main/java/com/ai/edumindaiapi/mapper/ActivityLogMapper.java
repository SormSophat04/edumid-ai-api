package com.ai.edumindaiapi.mapper;

import com.ai.edumindaiapi.common.dto.RecentActivityResponse;
import com.ai.edumindaiapi.domain.ActivityLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityLogMapper {
    @Mapping(target = "time", ignore = true)
    @Mapping(target = "icon", source = "icon")
    RecentActivityResponse.ActivityEntry toActivityEntry(ActivityLog log);
}
