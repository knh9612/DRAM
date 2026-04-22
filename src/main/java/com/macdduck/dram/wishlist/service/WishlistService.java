package com.macdduck.dram.wishlist.service;

import com.macdduck.dram.global.exception.BusinessException;
import com.macdduck.dram.global.exception.ErrorCode;
import com.macdduck.dram.user.entity.User;
import com.macdduck.dram.user.repository.UserRepository;
import com.macdduck.dram.whisky.entity.Whisky;
import com.macdduck.dram.whisky.service.WhiskyService;
import com.macdduck.dram.wishlist.dto.WishlistResponse;
import com.macdduck.dram.wishlist.entity.Wishlist;
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
    private final UserRepository userRepository;
    private final WhiskyService whiskyService;

    public List<WishlistResponse> getWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId).stream()
                .map(WishlistResponse::from)
                .toList();
    }

    @Transactional
    public Long addWishlist(Long userId, Long whiskyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Whisky whisky = whiskyService.getWhisky(whiskyId);

        if (wishlistRepository.existsByUserIdAndWhiskyId(userId, whiskyId)) {
            throw new BusinessException(ErrorCode.WISHLIST_ALREADY_EXISTS);
        }

        return wishlistRepository.save(Wishlist.builder()
                .user(user)
                .whisky(whisky)
                .build()).getId();
    }
}
