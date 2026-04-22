package com.macdduck.dram.wishlist.repository;

import com.macdduck.dram.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // fetch join으로 위스키 정보를 한 번의 쿼리로 함께 조회 (N+1 방지)
    @Query("SELECT w FROM Wishlist w JOIN FETCH w.whisky WHERE w.user.id = :userId ORDER BY w.createdAt DESC")
    List<Wishlist> findByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndWhiskyId(Long userId, Long whiskyId);
}
