package com.macdduck.dram.chat.dto;

import com.macdduck.dram.whisky.entity.Whisky;

public record RecommendedWhiskyResponse(
        Long id,
        String nameKo,
        String style,
        String country,
        String imageFile
) {
    public static RecommendedWhiskyResponse from(Whisky whisky) {
        return new RecommendedWhiskyResponse(
                whisky.getId(),
                whisky.getNameKo(),
                whisky.getStyle(),
                whisky.getCountry(),
                whisky.getImageFile()
        );
    }
}
