package com.macdduck.dram.note.entity;

import com.macdduck.dram.global.entity.BaseTimeEntity;
import com.macdduck.dram.global.enums.AftertasteType;
import com.macdduck.dram.global.enums.BodyType;
import com.macdduck.dram.global.enums.DrinkingMethod;
import com.macdduck.dram.global.enums.FlavorTag;
import com.macdduck.dram.user.entity.User;
import com.macdduck.dram.whisky.entity.Whisky;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tasting_notes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TastingNote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "whisky_id", nullable = false)
    private Whisky whisky;

    @Enumerated(EnumType.STRING)
    private DrinkingMethod drinkingMethod;

    @Enumerated(EnumType.STRING)
    private List<FlavorTag> selectedTags;

    @Enumerated(EnumType.STRING)
    private BodyType body;

    @Enumerated(EnumType.STRING)
    private AftertasteType aftertaste;

    @Column(nullable = false)
    private Double rating;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(columnDefinition = "TEXT")
    private String aiNote;

    @Builder
    private TastingNote(User user, Whisky whisky, DrinkingMethod drinkingMethod,
                       List<FlavorTag> selectedTags, BodyType body, AftertasteType aftertaste,
                       Double rating, String memo) {
        this.user = user;
        this.whisky = whisky;
        this.drinkingMethod = drinkingMethod;
        this.selectedTags = selectedTags;
        this.body = body;
        this.aftertaste = aftertaste;
        this.rating = rating;
        this.memo = memo;
    }

    public void updateAiNote(String aiNote) {
        this.aiNote = aiNote;
    }
}
