package com.macdduck.dram.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AftertasteType {

    SHORT("짧다"),
    MEDIUM("중간"),
    LONG("길다");

    private final String displayName;
}