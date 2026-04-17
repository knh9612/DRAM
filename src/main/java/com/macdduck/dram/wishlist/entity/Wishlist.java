package com.macdduck.dram.wishlist.entity;

import com.macdduck.dram.user.entity.User;
import com.macdduck.dram.whisky.entity.Whisky;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "wishlists",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_wishlists",
                columnNames = {"user_id", "whisky_id"}
        ))
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "whisky_id", nullable = false)
    private Whisky whisky;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Wishlist(User user, Whisky whisky) {
        this.user = user;
        this.whisky = whisky;
    }
}
