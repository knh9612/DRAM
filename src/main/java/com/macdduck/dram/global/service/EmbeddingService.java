package com.macdduck.dram.global.service;

import com.macdduck.dram.global.config.OpenAiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * OpenAI Embedding API를 호출하여 텍스트를 1536차원 벡터로 변환하는 서비스.
 *
 * 동작 원리:
 * 1. 텍스트를 OpenAI API에 전송
 * 2. text-embedding-3-small 모델이 텍스트의 의미를 분석
 * 3. 1536개의 숫자로 이루어진 벡터(배열)를 반환
 *
 * 예: "달달하고 부드러운" → [0.012, -0.034, 0.056, ..., 0.023]
 * 의미가 비슷한 텍스트는 비슷한 벡터를 가짐.
 */
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final OpenAiProperties openAiProperties;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.openai.com")
            .build();

    public float[] embed(String text) {
        Map<String, Object> body = Map.of(
                "model", openAiProperties.getEmbeddingModel(),
                "input", text
        );

        var response = restClient.post()
                .uri("/v1/embeddings")
                .header("Authorization", "Bearer " + openAiProperties.getApiKey())
                .header("Content-Type", "application/json")
                .body(body)
                .retrieve()
                .body(EmbeddingResponse.class);

        return response.data().get(0).embedding();
    }

    /**
     * OpenAI Embedding API 응답 구조:
     * {
     *   "data": [{ "embedding": [0.012, -0.034, ...] }]
     * }
     */
    private record EmbeddingResponse(List<EmbeddingData> data) {}
    private record EmbeddingData(float[] embedding) {}
}