package com.ai.edumindaiapi.repository;

import com.ai.edumindaiapi.domain.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<ActivityLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
    long countByType(String type);
    long count();
}
