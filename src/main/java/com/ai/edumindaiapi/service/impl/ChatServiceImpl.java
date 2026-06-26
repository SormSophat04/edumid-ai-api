package com.ai.edumindaiapi.service.impl;

import com.ai.edumindaiapi.common.dto.ChatConversationRequest;
import com.ai.edumindaiapi.common.dto.ChatConversationResponse;
import com.ai.edumindaiapi.common.dto.ChatMessageResponse;
import com.ai.edumindaiapi.common.dto.ChatSendRequest;
import com.ai.edumindaiapi.common.enums.MessageSender;
import com.ai.edumindaiapi.domain.ChatConversation;
import com.ai.edumindaiapi.domain.ChatMessage;
import com.ai.edumindaiapi.mapper.ChatMapper;
import com.ai.edumindaiapi.repository.ChatConversationRepository;
import com.ai.edumindaiapi.repository.ChatMessageRepository;
import com.ai.edumindaiapi.service.ChatService;
import com.ai.edumindaiapi.service.ai.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final ChatMapper chatMapper;
    private final AiService aiService;

    @Override
    public List<ChatConversationResponse> getConversations(Long userId) {
        List<ChatConversation> conversations = conversationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return conversations.stream()
                .map(chatMapper::toConversationResponse)
                .toList();
    }

    @Override
    @Transactional
    public ChatConversationResponse createConversation(Long userId, ChatConversationRequest request) {
        ChatConversation conversation = ChatConversation.builder()
                .userId(userId)
                .title(request.title())
                .build();
        conversation = conversationRepository.save(conversation);
        return chatMapper.toConversationResponse(conversation);
    }

    @Override
    public List<ChatMessageResponse> getMessages(Long conversationId) {
        List<ChatMessage> messages = messageRepository.findByConversationIdOrderByCreatedAt(conversationId);
        return messages.stream()
                .map(chatMapper::toMessageResponse)
                .toList();
    }

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(Long userId, ChatSendRequest request) {
        ChatConversation conversation = conversationRepository.findById(request.conversationId())
                .orElse(null);

        ChatMessage userMessage = ChatMessage.builder()
                .conversationId(request.conversationId())
                .sender(MessageSender.USER)
                .text(request.text())
                .build();
        messageRepository.save(userMessage);

        String context = conversation != null ? conversation.getTitle() : "General";
        List<Map<String, String>> history = new ArrayList<>();
        String aiReplyText = aiService.tutorReply(context, history, request.text());

        ChatMessage aiMessage = ChatMessage.builder()
                .conversationId(request.conversationId())
                .sender(MessageSender.AI)
                .text(aiReplyText)
                .build();
        aiMessage = messageRepository.save(aiMessage);

        return chatMapper.toMessageResponse(aiMessage);
    }
}
