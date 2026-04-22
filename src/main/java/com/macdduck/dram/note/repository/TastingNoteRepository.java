package com.macdduck.dram.note.repository;

import com.macdduck.dram.note.entity.TastingNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TastingNoteRepository extends JpaRepository<TastingNote, Long> {

    List<TastingNote> findByUserIdOrderByCreatedAtDesc(Long userId);
}
