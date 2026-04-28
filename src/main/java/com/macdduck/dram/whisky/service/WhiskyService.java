package com.macdduck.dram.whisky.service;

import com.macdduck.dram.global.exception.BusinessException;
import com.macdduck.dram.global.exception.ErrorCode;
import com.macdduck.dram.global.service.EmbeddingService;
import com.macdduck.dram.whisky.dto.WhiskyDetailResponse;
import com.macdduck.dram.whisky.dto.WhiskyListResponse;
import com.macdduck.dram.whisky.entity.Whisky;
import com.macdduck.dram.whisky.repository.WhiskyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WhiskyService {

    private final WhiskyRepository whiskyRepository;
    private final EmbeddingService embeddingService;

    private static final double SIMILARITY_THRESHOLD = 1.0;
    private static final int MAX_RESULTS = 30;
    private static final int PAGE_SIZE = 10;

    public Whisky getWhisky(Long whiskyId) {
        return whiskyRepository.findById(whiskyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WHISKY_NOT_FOUND));
    }

    public WhiskyDetailResponse getWhiskyDetail(Long whiskyId) {
        return WhiskyDetailResponse.from(getWhisky(whiskyId));
    }

    public List<WhiskyListResponse> getPopularWhiskies() {
        return whiskyRepository.findTop5ByWishlistCount().stream()
                .map(WhiskyListResponse::from)
                .toList();
    }

    /**
     * 자연어 검색 흐름:
     * 1. 검색어를 OpenAI API로 보내서 1536차원 벡터로 변환
     * 2. 변환된 벡터를 PostgreSQL 문자열 형식으로 변환 (pgvector가 인식하는 포맷: "[0.01, -0.03, ...]")
     * 3. pgvector의 <=> 연산자로 DB에 저장된 위스키 벡터들과 코사인 거리 비교
     * 4. 임계값(0.5) 이하인 결과만, 유사도 높은 순으로 반환
     */
    public List<Whisky> searchByVector(float[] vector, int limit) {
        return whiskyRepository.searchByEmbedding(toVectorString(vector), SIMILARITY_THRESHOLD, limit);
    }

    public List<WhiskyListResponse> search(String query, int page) {
        float[] queryEmbedding = embeddingService.embed(query);

        String embeddingStr = toVectorString(queryEmbedding);

        int offset = page * PAGE_SIZE;
        int limit = Math.min(PAGE_SIZE, MAX_RESULTS - offset);

        if (limit <= 0) {
            return List.of();
        }

        return whiskyRepository.searchByEmbedding(embeddingStr, SIMILARITY_THRESHOLD, MAX_RESULTS)
                .stream()
                .skip(offset)
                .limit(limit)
                .map(WhiskyListResponse::from)
                .toList();
    }

    /**
     * float 배열을 pgvector가 인식하는 문자열로 변환.
     * [0.01, -0.03, 0.05] → "[0.01,-0.03,0.05]"
     */
    private String toVectorString(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(embedding[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
