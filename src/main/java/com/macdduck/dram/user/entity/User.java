package com.macdduck.dram.user.entity;

import com.macdduck.dram.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_users_oauth",
                columnNames = {"oauthProvider", "oauthId"}
        ))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column
    private String email;

    @Column(columnDefinition = "TEXT DEFAULT 'default.png'")
    private String profileImage;

    @Column(nullable = false, length = 20)
    private String oauthProvider;

    @Column(nullable = false)
    private String oauthId;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    @Column(nullable = false)
    private Boolean reminderEnabled = false;

    @Column(columnDefinition = "TEXT")
    private String fcmToken;

    @Builder
    private User(String nickname, String email, String profileImage,
                String oauthProvider, String oauthId) {
        this.nickname = nickname;
        this.email = email;
        this.profileImage = profileImage != null ? profileImage : "default.png";
        this.oauthProvider = oauthProvider;
        this.oauthId = oauthId;
        this.reminderEnabled = false;
    }
}
