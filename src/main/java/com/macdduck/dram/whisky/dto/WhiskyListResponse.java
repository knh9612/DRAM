package com.macdduck.dram.whisky.dto;

import com.macdduck.dram.whisky.entity.Whisky;

public record WhiskyListResponse(
        Long id,
        String nameEn,
        String nameKo,
        String imageFile,
        String style,
        String country,
        String region,
        Double alcoholByVolume,
        Integer price
) {
    public static WhiskyListResponse from(Whisky whisky) {
        return new WhiskyListResponse(
                whisky.getId(),
                whisky.getNameEn(),
                whisky.getNameKo(),
                whisky.getImageFile(),
                whisky.getStyle(),
                whisky.getCountry(),
                whisky.getRegion(),
                whisky.getAlcoholByVolume(),
                whisky.getPrice()
        );
    }
}
