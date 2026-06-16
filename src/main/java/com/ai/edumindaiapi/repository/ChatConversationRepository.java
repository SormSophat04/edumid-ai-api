package com.ai.edumindaiapi.repository;

import com.ai.edumindaiapi.domain.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    List<ChatConversation> findByUserIdOrderByCreatedAtDesc(Long userId);
}
