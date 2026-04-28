package com.macdduck.dram.note.repository;

import com.macdduck.dram.note.entity.TastingNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TastingNoteRepository extends JpaRepository<TastingNote, Long> {

    List<TastingNote> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query(value = "SELECT embedding::text FROM tasting_notes WHERE user_id = :userId AND rating >= 4 AND embedding IS NOT NULL", nativeQuery = true)
    List<String> findHighRatedEmbeddingsByUserId(@Param("userId") Long userId);
}
