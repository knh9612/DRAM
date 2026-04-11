package com.macdduck.dram.whisky.entity;

import com.macdduck.dram.global.entity.BaseTimeEntity;
import com.macdduck.dram.global.enums.AftertasteType;
import com.macdduck.dram.global.enums.BodyType;
import com.macdduck.dram.global.enums.FlavorTag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "whiskies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Whisky extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nameEn;

    @Column(nullable = false)
    private String nameKo;

    private String imageFile;

    private Double alcoholByVolume;

    @Column(length = 50)
    private String style;

    @Column(length = 50)
    private String country;

    @Column(length = 50)
    private String region;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private List<FlavorTag> noseTags;

    @Enumerated(EnumType.STRING)
    private List<FlavorTag> palateTags;

    @Enumerated(EnumType.STRING)
    private BodyType body;

    @Enumerated(EnumType.STRING)
    private AftertasteType aftertaste;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String embeddingText;

    @Builder
    private Whisky(String nameEn, String nameKo, String imageFile, Double alcoholByVolume,
                  String style, String country, String region, Integer price,
                  List<FlavorTag> noseTags, List<FlavorTag> palateTags,
                  BodyType body, AftertasteType aftertaste, String description,
                  String embeddingText) {
        this.nameEn = nameEn;
        this.nameKo = nameKo;
        this.imageFile = imageFile;
        this.alcoholByVolume = alcoholByVolume;
        this.style = style;
        this.country = country;
        this.region = region;
        this.price = price;
        this.noseTags = noseTags;
        this.palateTags = palateTags;
        this.body = body;
        this.aftertaste = aftertaste;
        this.description = description;
        this.embeddingText = embeddingText;
    }
}
