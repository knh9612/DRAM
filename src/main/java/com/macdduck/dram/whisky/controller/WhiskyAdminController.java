package com.macdduck.dram.whisky.controller;

import com.macdduck.dram.whisky.service.WhiskyEmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 관리자용 위스키 API.
 * 위스키 데이터의 임베딩을 수동으로 생성할 때 사용.
 */
@RestController
@RequestMapping("/api/admin/whiskies")
@RequiredArgsConstructor
public class WhiskyAdminController {

    private final WhiskyEmbeddingService whiskyEmbeddingService;

    @PostMapping("/embeddings")
    public ResponseEntity<Map<String, Object>> generateEmbeddings() {
        int count = whiskyEmbeddingService.generateMissingEmbeddings();
        return ResponseEntity.ok(Map.of(
                "message", "임베딩 생성 완료",
                "count", count
        ));
    }
}