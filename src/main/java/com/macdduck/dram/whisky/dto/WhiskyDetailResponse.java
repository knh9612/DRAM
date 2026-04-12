package com.macdduck.dram.whisky.dto;

import com.macdduck.dram.global.enums.AftertasteType;
import com.macdduck.dram.global.enums.BodyType;
import com.macdduck.dram.global.enums.FlavorTag;
import com.macdduck.dram.whisky.entity.Whisky;

import java.util.List;

public record WhiskyDetailResponse(
        Long id,
        String nameEn,
        String nameKo,
        String imageFile,
        Double alcoholByVolume,
        String style,
        String country,
        String region,
        Integer price,
        List<FlavorTag> noseTags,
        List<FlavorTag> palateTags,
        BodyType body,
        AftertasteType aftertaste,
        String description
) {
    public static WhiskyDetailResponse from(Whisky whisky) {
        return new WhiskyDetailResponse(
                whisky.getId(),
                whisky.getNameEn(),
                whisky.getNameKo(),
                whisky.getImageFile(),
                whisky.getAlcoholByVolume(),
                whisky.getStyle(),
                whisky.getCountry(),
                whisky.getRegion(),
                whisky.getPrice(),
                whisky.getNoseTags(),
                whisky.getPalateTags(),
                whisky.getBody(),
                whisky.getAftertaste(),
                whisky.getDescription()
        );
    }
}