package com.macdduck.dram.note.dto;

import com.macdduck.dram.global.enums.AftertasteType;
import com.macdduck.dram.global.enums.BodyType;
import com.macdduck.dram.global.enums.DrinkingMethod;
import com.macdduck.dram.global.enums.FlavorTag;

import java.util.List;

public record AiNoteRequest(
        Long whiskyId,
        DrinkingMethod drinkingMethod,
        List<FlavorTag> selectedTags,
        BodyType body,
        AftertasteType aftertaste
) {
}
