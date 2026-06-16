package com.ai.edumindaiapi.repository;

import com.ai.edumindaiapi.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByConversationIdOrderByCreatedAt(Long conversationId);
    long countByConversationId(Long conversationId);
}
