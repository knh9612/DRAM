package com.macdduck.dram.whisky.service;

import com.macdduck.dram.whisky.dto.WhiskyListResponse;
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

    public List<WhiskyListResponse> getPopularWhiskies() {
        return whiskyRepository.findTop5ByWishlistCount().stream()
                .map(WhiskyListResponse::from)
                .toList();
    }
}
