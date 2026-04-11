package com.macdduck.dram.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DrinkingMethod {

    NEAT("니트"),
    ON_THE_ROCKS("온더락"),
    HIGHBALL("하이볼"),
    WATER("물 약간");

    private final String displayName;
}
