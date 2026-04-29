package com.macdduck.dram.chat.controller;

import com.macdduck.dram.chat.dto.ChatMessageRequest;
import com.macdduck.dram.chat.dto.ChatStartRequest;
import com.macdduck.dram.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

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
