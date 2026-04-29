package com.macdduck.dram.chat.dto;

import java.util.List;

public record ChatMessageResponse(
        String role,
        String content,
        List<RecommendedWhiskyResponse> recommendations
) {
}