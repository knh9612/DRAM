package com.macdduck.dram.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FlavorTag {

    HONEY("꿀"),
    VANILLA("바닐라"),
    CARAMEL("캐러멜"),
    CHOCOLATE("초콜릿"),

    APPLE_PEAR("사과/배"),
    CITRUS("감귤/레몬"),
    BERRY("베리"),
    TROPICAL("열대과일"),
    DRIED_FRUIT("건과일"),

    MALT("몰트/보리"),
    COFFEE("커피"),

    CINNAMON("시나몬"),
    PEPPER("후추"),
    GINGER("진저"),
    CHILI("고추"),

    SMOKY("스모키"),
    MEDICINAL("약품"),
    EARTHY("흙"),

    FLORAL("꽃향"),
    HERBAL("허브"),

    OAK("오크"),
    CREAMY("크림/버터"),
    NUTTY("견과류"),
    MARITIME("바다/소금");

    private final String displayName;
}
