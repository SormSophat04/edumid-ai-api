package com.ai.edumindaiapi.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RateLimitingFilter implements Filter {

    private final Map<String, UserRateLimit> rateLimitMap = new ConcurrentHashMap<>();

    private static final long MAX_REQUESTS = 10;
    private static final long WINDOW_MS = 60_000;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = request.getRequestURI();

        if (!path.equals("/api/quiz/generate") && !path.equals("/api/chat/send")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String userId = "anonymous";
        if (request.getUserPrincipal() != null) {
            userId = request.getUserPrincipal().getName();
        }

        String key = userId + ":" + path;
        UserRateLimit rateLimit = rateLimitMap.computeIfAbsent(key, k -> new UserRateLimit());

        if (!rateLimit.tryAcquire()) {
            log.warn("Rate limit exceeded for user {} on path {}", userId, path);
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Too many requests. Please try again later.\",\"status\":429}");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private static class UserRateLimit {
        private final AtomicLong requestCount = new AtomicLong(0);
        private volatile long windowStart = System.currentTimeMillis();

        synchronized boolean tryAcquire() {
            long now = System.currentTimeMillis();
            if (now - windowStart > WINDOW_MS) {
                windowStart = now;
                requestCount.set(0);
            }
            return requestCount.incrementAndGet() <= MAX_REQUESTS;
        }
    }
}
