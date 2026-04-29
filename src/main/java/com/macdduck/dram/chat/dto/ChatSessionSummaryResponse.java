package com.macdduck.dram.chat.dto;

import com.macdduck.dram.chat.model.ChatSession;

public record ChatSessionSummaryResponse(
        String sessionId,
        String title,
        String updatedAt
) {
    public static ChatSessionSummaryResponse from(ChatSession session) {
        return new ChatSessionSummaryResponse(
                session.getSk().replace("SESSION#", ""),
                session.getTitle(),
                session.getUpdatedAt()
        );
    }
}