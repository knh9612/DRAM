package com.macdduck.dram.wishlist.dto;

import com.macdduck.dram.whisky.entity.Whisky;
import com.macdduck.dram.wishlist.entity.Wishlist;

import java.time.LocalDateTime;

public record WishlistResponse(
        Long id,
        Long whiskyId,
        String whiskyNameKo,
        String whiskyImageFile,
        String whiskyStyle,
        String whiskyCountry,
        String whiskyRegion,
        Double whiskyAlcoholByVolume,
        LocalDateTime createdAt
) {
    public static WishlistResponse from(Wishlist wishlist) {
        Whisky whisky = wishlist.getWhisky();
        return new WishlistResponse(
                wishlist.getId(),
                whisky.getId(),
                whisky.getNameKo(),
                whisky.getImageFile(),
                whisky.getStyle(),
                whisky.getCountry(),
                whisky.getRegion(),
                whisky.getAlcoholByVolume(),
                wishlist.getCreatedAt()
        );
    }
}
