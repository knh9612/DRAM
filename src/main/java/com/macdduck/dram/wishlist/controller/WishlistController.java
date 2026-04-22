package com.macdduck.dram.wishlist.controller;

import com.macdduck.dram.wishlist.dto.WishlistResponse;
import com.macdduck.dram.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * 내 위시리스트 목록 조회.
     * GET /api/wishlists?userId=1
     * TODO: 인증 구현 후 @AuthenticationPrincipal로 교체
     */
    @GetMapping
    public ResponseEntity<List<WishlistResponse>> getWishlist(@RequestParam Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlist(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> addWishlist(@RequestParam Long userId, @RequestParam Long whiskyId) {
        return Map.of("id", wishlistService.addWishlist(userId, whiskyId));
    }
}
