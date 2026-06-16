package com.ai.edumindaiapi.service;

import com.ai.edumindaiapi.common.dto.ChatConversationRequest;
import com.ai.edumindaiapi.common.dto.ChatConversationResponse;
import com.ai.edumindaiapi.common.dto.ChatMessageResponse;
import com.ai.edumindaiapi.common.dto.ChatSendRequest;
import java.util.List;

public interface ChatService {
    List<ChatConversationResponse> getConversations(Long userId);
    ChatConversationResponse createConversation(Long userId, ChatConversationRequest request);
    List<ChatMessageResponse> getMessages(Long conversationId);
    ChatMessageResponse sendMessage(Long userId, ChatSendRequest request);
}
