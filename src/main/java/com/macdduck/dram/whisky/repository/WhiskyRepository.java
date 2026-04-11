package com.macdduck.dram.whisky.repository;

import com.macdduck.dram.whisky.entity.Whisky;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WhiskyRepository extends JpaRepository<Whisky, Long> {

    @Query("SELECT w FROM Whisky w LEFT JOIN Wishlist wl ON w.id = wl.whisky.id GROUP BY w ORDER BY COUNT(wl) DESC LIMIT 5")
    List<Whisky> findTop5ByWishlistCount();

    /**
     * pgvector 코사인 거리 검색.
     *
     * <=> 연산자: 코사인 거리 = 1 - 코사인 유사도
     * - 값이 0에 가까울수록 유사 (거리가 가까움)
     * - 값이 1에 가까울수록 무관 (거리가 멀음)
     *
     * 따라서 threshold 0.5 이하 = 유사도 0.5 이상인 결과만 반환.
     * casting: ::vector는 PostgreSQL에서 문자열을 vector 타입으로 변환.
     */
    @Query(value = """
            SELECT w.*, (w.embedding <=> CAST(:embedding AS vector)) AS distance
            FROM whiskies w
            WHERE w.embedding IS NOT NULL
              AND (w.embedding <=> CAST(:embedding AS vector)) <= :threshold
            ORDER BY distance ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<Whisky> searchByEmbedding(
            @Param("embedding") String embedding,
            @Param("threshold") double threshold,
            @Param("limit") int limit
    );
}
