package com.macdduck.dram.note.dto;

import com.macdduck.dram.global.enums.AftertasteType;
import com.macdduck.dram.global.enums.BodyType;
import com.macdduck.dram.global.enums.DrinkingMethod;
import com.macdduck.dram.global.enums.FlavorTag;
import com.macdduck.dram.note.entity.TastingNote;

import java.time.LocalDateTime;
import java.util.List;

public record TastingNoteResponse(
        Long id,
        Long whiskyId,
        String whiskyNameKo,
        String whiskyImageFile,
        String whiskyStyle,
        String whiskyRegion,
        Double whiskyAlcoholByVolume,
        DrinkingMethod drinkingMethod,
        List<FlavorTag> selectedTags,
        BodyType body,
        AftertasteType aftertaste,
        Double rating,
        String memo,
        String aiNote,
        LocalDateTime createdAt
) {
    public static TastingNoteResponse from(TastingNote note) {
        return new TastingNoteResponse(
                note.getId(),
                note.getWhisky().getId(),
                note.getWhisky().getNameKo(),
                note.getWhisky().getImageFile(),
                note.getWhisky().getStyle(),
                note.getWhisky().getRegion(),
                note.getWhisky().getAlcoholByVolume(),
                note.getDrinkingMethod(),
                note.getSelectedTags(),
                note.getBody(),
                note.getAftertaste(),
                note.getRating(),
                note.getMemo(),
                note.getAiNote(),
                note.getCreatedAt()
        );
    }
}
