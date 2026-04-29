package com.macdduck.dram.chat.controller;

import com.macdduck.dram.chat.dto.ChatMessageRequest;
import com.macdduck.dram.chat.dto.ChatSessionMessagesResponse;
import com.macdduck.dram.chat.dto.ChatSessionSummaryResponse;
import com.macdduck.dram.chat.dto.ChatStartRequest;
import com.macdduck.dram.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/sessions")
    public List<ChatSessionSummaryResponse> getSessions(@RequestParam Long userId) {
        return chatService.getSessions(userId);
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ChatSessionMessagesResponse getSessionMessages(
            @RequestParam Long userId,
            @PathVariable String sessionId
    ) {
        return chatService.getSessionMessages(userId, sessionId);
    }

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter startChat(
            @RequestParam Long userId,
            @RequestBody ChatStartRequest request
    ) {
        return chatService.startChat(userId, request.message());
    }

    @PostMapping(value = "/{sessionId}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessage(
            @RequestParam Long userId,
            @PathVariable String sessionId,
            @RequestBody ChatMessageRequest request
    ) {
        return chatService.sendMessage(userId, sessionId, request.message());
    }
}
