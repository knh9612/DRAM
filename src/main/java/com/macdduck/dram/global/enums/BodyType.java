package com.macdduck.dram.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BodyType {

    LIGHT("라이트"),
    MEDIUM("미디엄"),
    FULL("풀바디");

    private final String displayName;
}
