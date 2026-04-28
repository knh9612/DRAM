package com.macdduck.dram.chat.service;

import com.macdduck.dram.chat.dto.RecommendedWhiskyResponse;
import com.macdduck.dram.chat.model.ChatMessage;
import com.macdduck.dram.chat.model.ChatSession;
import com.macdduck.dram.chat.repository.ChatRepository;
import com.macdduck.dram.global.config.OpenAiProperties;
import com.macdduck.dram.global.service.EmbeddingService;
import com.macdduck.dram.note.repository.TastingNoteRepository;
import com.macdduck.dram.whisky.entity.Whisky;
import com.macdduck.dram.whisky.service.WhiskyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final int MAX_SESSIONS = 5;
    private static final int AI_SEARCH_LIMIT = 5;
    private static final int PREFERENCE_PRIOR_K = 5;
    private static final double MAX_PREFERENCE_WEIGHT = 0.4;
    private final ChatRepository chatRepository;
    private final WhiskyService whiskyService;
    private final EmbeddingService embeddingService;
    private final TastingNoteRepository tastingNoteRepository;
    private final OpenAiProperties openAiProperties;
    private final tools.jackson.databind.ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.openai.com")
            .build();

    @Transactional(readOnly = true)
    public SseEmitter startChat(Long userId, String message) {
        float[] queryVector = embeddingService.embed(message);
        float[] finalVector = blendWithPreference(userId, queryVector);
        List<Whisky> whiskies = whiskyService.searchByVector(finalVector, AI_SEARCH_LIMIT);

        List<RecommendedWhiskyResponse> recommendations = whiskies.stream()
                .map(RecommendedWhiskyResponse::from)
                .toList();
        List<Long> recommendedIds = whiskies.stream().map(Whisky::getId).toList();

        SseEmitter emitter = new SseEmitter(180_000L);
        emitter.onTimeout(emitter::complete);
        emitter.onError(e -> emitter.complete());

        CompletableFuture.runAsync(() -> {
            try {
                String sessionId = initSession(userId, message);
                saveMessage(sessionId, "user", message, List.of());

                emitter.send(SseEmitter.event().name("recommendations").data(recommendations));

                StringBuilder content = new StringBuilder();
                streamGptResponse(message, whiskies, token -> {
                    content.append(token);
                    emitter.send(SseEmitter.event().name("token").data(Map.of("token", token)));
                });

                saveMessage(sessionId, "assistant", content.toString(), recommendedIds);

                String now = LocalDateTime.now().toString();
                chatRepository.updateSessionTimestamp(userId, sessionId, now);

                emitter.send(SseEmitter.event().name("done").data(Map.of("sessionId", sessionId)));
                emitter.complete();

            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private String initSession(Long userId, String firstMessage) {
        List<ChatSession> sessions = chatRepository.getSessionsByUser(userId);

        if (sessions.size() >= MAX_SESSIONS) {
            ChatSession oldest = sessions.stream()
                    .min(Comparator.comparing(ChatSession::getCreatedAt))
                    .orElseThrow();
            String oldestSessionId = oldest.getSk().replace("SESSION#", "");
            chatRepository.deleteSessionAndMessages(userId, oldestSessionId);
        }

        String sessionId = "sess_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String title = firstMessage.length() > 15 ? firstMessage.substring(0, 15) : firstMessage;
        String now = LocalDateTime.now().toString();
        long ttl = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();

        chatRepository.saveSession(ChatSession.builder()
                .pk("USER#" + userId)
                .sk("SESSION#" + sessionId)
                .type("session")
                .title(title)
                .createdAt(now)
                .updatedAt(now)
                .ttl(ttl)
                .build());

        return sessionId;
    }

    private void saveMessage(String sessionId, String role, String content, List<Long> recommendedIds) {
        String timestamp = LocalDateTime.now().toString();
        long ttl = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();

        chatRepository.saveMessage(ChatMessage.builder()
                .pk("SESSION#" + sessionId)
                .sk("MSG#" + timestamp)
                .type("message")
                .role(role)
                .content(content)
                .recommendedIds(recommendedIds.isEmpty() ? null : recommendedIds)
                .ttl(ttl)
                .build());
    }

    private float[] blendWithPreference(Long userId, float[] queryVector) {
        List<String> embeddingStrings = tastingNoteRepository.findHighRatedEmbeddingsByUserId(userId);
        if (embeddingStrings.isEmpty()) {
            return queryVector;
        }

        List<float[]> embeddings = embeddingStrings.stream().map(this::parseVector).toList();
        float[] preferenceVector = averageVectors(embeddings);

        int n = embeddings.size();
        double preferenceWeight = Math.min(MAX_PREFERENCE_WEIGHT, (double) n / (n + PREFERENCE_PRIOR_K));
        double queryWeight = 1.0 - preferenceWeight;

        float[] result = new float[queryVector.length];
        for (int i = 0; i < queryVector.length; i++) {
            result[i] = (float) (queryWeight * queryVector[i] + preferenceWeight * preferenceVector[i]);
        }
        return result;
    }

    private float[] parseVector(String vectorStr) {
        String[] parts = vectorStr.substring(1, vectorStr.length() - 1).split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i].trim());
        }
        return result;
    }

    private float[] averageVectors(List<float[]> vectors) {
        int dim = vectors.get(0).length;
        float[] avg = new float[dim];
        for (float[] v : vectors) {
            for (int i = 0; i < dim; i++) avg[i] += v[i];
        }
        for (int i = 0; i < dim; i++) avg[i] /= vectors.size();
        return avg;
    }

    private void streamGptResponse(String userMessage, List<Whisky> whiskies, TokenCallback callback) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", buildSystemPrompt(whiskies)));
        messages.add(Map.of("role", "user", "content", userMessage));

        Map<String, Object> body = Map.of(
                "model", openAiProperties.getChatModel(),
                "stream", true,
                "messages", messages
        );

        restClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + openAiProperties.getApiKey())
                .header("Content-Type", "application/json")
                .body(body)
                .exchange((req, res) -> {
                    try (var reader = new BufferedReader(new InputStreamReader(res.getBody()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.startsWith("data: ") || line.equals("data: [DONE]")) continue;
                            String token = extractToken(line.substring(6));
                            if (token != null) callback.accept(token);
                        }
                    }
                    return null;
                });
    }

    private String extractToken(String json) {
        try {
            StreamChunk chunk = objectMapper.readValue(json, StreamChunk.class);
            if (chunk.choices() == null || chunk.choices().isEmpty()) return null;
            StreamDelta delta = chunk.choices().get(0).delta();
            return (delta != null) ? delta.content() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String buildSystemPrompt(List<Whisky> whiskies) {
        StringBuilder sb = new StringBuilder("""
                당신은 친근한 위스키 소믈리에입니다. 아래 위스키 정보를 참고하여 위스키를 잘 모르는 입문자도 이해하기 쉽게 추천해주세요.
                3개 내외로 추천하고, 각 위스키의 특징을 간략히 설명해주세요.
                
                추천 위스키 목록:
                """);

        for (int i = 0; i < whiskies.size(); i++) {
            Whisky w = whiskies.get(i);
            sb.append(String.format("%d. %s (%s) — %s, %s%n", i + 1, w.getNameKo(), w.getNameEn(), w.getStyle(), w.getCountry()));
            if (w.getDescription() != null) sb.append("   ").append(w.getDescription()).append("\n");
        }

        return sb.toString();
    }

    @FunctionalInterface
    private interface TokenCallback {
        void accept(String token) throws IOException;
    }

    private record StreamChunk(List<StreamChoice> choices) {
    }

    private record StreamChoice(StreamDelta delta) {
    }

    private record StreamDelta(String content) {
    }
}
