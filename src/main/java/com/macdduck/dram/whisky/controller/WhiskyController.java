package com.macdduck.dram.whisky.controller;

import com.macdduck.dram.whisky.dto.WhiskyListResponse;
import com.macdduck.dram.whisky.service.WhiskyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/whiskies")
@RequiredArgsConstructor
public class WhiskyController {

    private final WhiskyService whiskyService;

    @GetMapping("/popular")
    public ResponseEntity<List<WhiskyListResponse>> getPopularWhiskies() {
        return ResponseEntity.ok(whiskyService.getPopularWhiskies());
    }

    /**
     * 자연어 검색 API.
     * GET /api/whiskies/search?query=달달하고 부드러운&page=0
     *
     * @param query 검색어 (자연어)
     * @param page  페이지 번호 (0부터 시작, 페이지당 10개)
     */
    @GetMapping("/search")
    public ResponseEntity<List<WhiskyListResponse>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(whiskyService.search(query, page));
    }
}
