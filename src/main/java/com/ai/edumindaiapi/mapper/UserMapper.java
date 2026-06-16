package com.ai.edumindaiapi.mapper;

import com.ai.edumindaiapi.common.dto.AuthResponse;
import com.ai.edumindaiapi.common.dto.RegisterRequest;
import com.ai.edumindaiapi.common.dto.UserResponse;
import com.ai.edumindaiapi.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request);

    @Mapping(target = "token", source = "token")
    @Mapping(target = "userId", source = "user.id")
    AuthResponse toAuthResponse(User user, String token);

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserResponse toUserResponse(User user);
}
