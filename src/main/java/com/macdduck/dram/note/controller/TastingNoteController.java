package com.macdduck.dram.note.controller;

import com.macdduck.dram.note.dto.AiNoteRequest;
import com.macdduck.dram.note.service.TastingNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class TastingNoteController {

    private final TastingNoteService tastingNoteService;

    @PostMapping("/ai-note")
    public Map<String, String> generateAiNote(@RequestBody AiNoteRequest request) {
        String aiNote = tastingNoteService.generateAiNote(request);
        return Map.of("aiNote", aiNote);
    }
}