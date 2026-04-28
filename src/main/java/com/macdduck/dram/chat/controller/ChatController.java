package com.macdduck.dram.chat.controller;

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
}
