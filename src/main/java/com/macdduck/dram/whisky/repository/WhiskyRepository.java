package com.macdduck.dram.whisky.repository;

import com.macdduck.dram.whisky.entity.Whisky;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WhiskyRepository extends JpaRepository<Whisky, Long> {

    @Query("SELECT w FROM Whisky w LEFT JOIN Wishlist wl ON w.id = wl.whisky.id GROUP BY w ORDER BY COUNT(wl) DESC LIMIT 5")
    List<Whisky> findTop5ByWishlistCount();
}
