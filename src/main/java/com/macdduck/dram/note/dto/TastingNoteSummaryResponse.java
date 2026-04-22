package com.macdduck.dram.note.dto;

import com.macdduck.dram.note.entity.TastingNote;

import java.time.LocalDateTime;

public record TastingNoteSummaryResponse(
        Long id,
        Long whiskyId,
        String whiskyNameKo,
        String whiskyImageFile,
        String whiskyStyle,
        String whiskyRegion,
        Double whiskyAlcoholByVolume,
        Double rating,
        LocalDateTime createdAt
) {
    public static TastingNoteSummaryResponse from(TastingNote note) {
        return new TastingNoteSummaryResponse(
                note.getId(),
                note.getWhisky().getId(),
                note.getWhisky().getNameKo(),
                note.getWhisky().getImageFile(),
                note.getWhisky().getStyle(),
                note.getWhisky().getRegion(),
                note.getWhisky().getAlcoholByVolume(),
                note.getRating(),
                note.getCreatedAt()
        );
    }
}
