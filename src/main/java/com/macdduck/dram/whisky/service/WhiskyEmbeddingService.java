package com.macdduck.dram.whisky.service;

import com.macdduck.dram.global.service.EmbeddingService;
import com.macdduck.dram.whisky.entity.Whisky;
import com.macdduck.dram.whisky.repository.WhiskyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 위스키 데이터에 임베딩 벡터를 생성하는 서비스.
 *
 * Hibernate는 pgvector의 vector 타입을 모르기 때문에,
 * JdbcTemplate으로 직접 SQL을 실행하여 embedding 컬럼을 업데이트한다.
 *
 * 이 서비스는 위스키 데이터를 처음 넣거나 embeddingText가 변경됐을 때 실행.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WhiskyEmbeddingService {

    private final WhiskyRepository whiskyRepository;
    private final EmbeddingService embeddingService;
    private final JdbcTemplate jdbcTemplate;

    /**
     * embedding이 없는 위스키들의 임베딩을 생성.
     * embeddingText → OpenAI API → 1536차원 벡터 → DB 저장
     */
    @Transactional
    public int generateMissingEmbeddings() {
        List<Whisky> whiskies = whiskyRepository.findAll().stream()
                .filter(w -> w.getEmbeddingText() != null)
                .toList();

        int count = 0;
        for (Whisky whisky : whiskies) {
            try {
                float[] embedding = embeddingService.embed(whisky.getEmbeddingText());
                String vectorStr = toVectorString(embedding);

                jdbcTemplate.update(
                        "UPDATE whiskies SET embedding = CAST(? AS vector) WHERE id = ?",
                        vectorStr, whisky.getId()
                );
                count++;
                log.info("임베딩 생성 완료: {} (ID: {})", whisky.getNameKo(), whisky.getId());
            } catch (Exception e) {
                log.error("임베딩 생성 실패: {} (ID: {})", whisky.getNameKo(), whisky.getId(), e);
            }
        }

        log.info("총 {}개 위스키 임베딩 생성 완료", count);
        return count;
    }

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