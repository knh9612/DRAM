package com.macdduck.dram.whisky.controller;

import com.macdduck.dram.whisky.dto.WhiskyListResponse;
import com.macdduck.dram.whisky.service.WhiskyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
