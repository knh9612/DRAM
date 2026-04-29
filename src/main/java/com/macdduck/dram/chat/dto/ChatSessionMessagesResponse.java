package com.macdduck.dram.chat.dto;

import java.util.List;

public record ChatSessionMessagesResponse(
        String sessionId,
        String title,
        List<ChatMessageResponse> messages
) {
}