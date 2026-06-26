package com.ai.edumindaiapi.controller;

import com.ai.edumindaiapi.common.dto.*;
import com.ai.edumindaiapi.security.AuthUser;
import com.ai.edumindaiapi.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/conversations")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ChatConversationResponse>>> getConversations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(chatService.getConversations(userId)));
    }

    @PostMapping("/conversations")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ChatConversationResponse>> createConversation(
            @Valid @RequestBody ChatConversationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(chatService.createConversation(userId, request)));
    }

    @GetMapping("/conversations/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMessages(id)));
    }

    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessage(@Valid @RequestBody ChatSendRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((AuthUser) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(ApiResponse.ok(chatService.sendMessage(userId, request)));
    }
}
