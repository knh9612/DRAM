package com.macdduck.dram.note.controller;

import com.macdduck.dram.note.dto.AiNoteRequest;
import com.macdduck.dram.note.dto.TastingNoteCreateRequest;
import com.macdduck.dram.note.service.TastingNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class TastingNoteController {

    private final TastingNoteService tastingNoteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> createNote(
            @RequestParam Long userId,
            @RequestBody TastingNoteCreateRequest request
    ) {
        Long noteId = tastingNoteService.createNote(userId, request);
        return Map.of("id", noteId);
    }

    @PostMapping("/ai-note")
    public Map<String, String> generateAiNote(@RequestBody AiNoteRequest request) {
        String aiNote = tastingNoteService.generateAiNote(request);
        return Map.of("aiNote", aiNote);
    }
}