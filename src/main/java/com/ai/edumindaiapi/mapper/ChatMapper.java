package com.ai.edumindaiapi.mapper;

import com.ai.edumindaiapi.common.dto.ChatConversationResponse;
import com.ai.edumindaiapi.common.dto.ChatMessageResponse;
import com.ai.edumindaiapi.domain.ChatConversation;
import com.ai.edumindaiapi.domain.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    @Mapping(target = "sender", expression = "java(message.getSender().name())")
    ChatMessageResponse toMessageResponse(ChatMessage message);

    ChatConversationResponse toConversationResponse(ChatConversation conversation);
}
