package com.ai.edumindaiapi.service;

import com.ai.edumindaiapi.domain.User;
import com.ai.edumindaiapi.common.dto.RegisterRequest;

import java.util.Optional;

public interface UserService {
    User register(RegisterRequest request);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
