package com.macdduck.dram.wishlist.service;

import com.macdduck.dram.wishlist.dto.WishlistResponse;
import com.macdduck.dram.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    // userId에 해당하는 위시리스트 목록을 조회해서 DTO로 변환
    public List<WishlistResponse> getWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId).stream()
                .map(WishlistResponse::from)
                .toList();
    }
}
