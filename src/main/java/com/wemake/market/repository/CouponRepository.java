package com.wemake.market.repository;

import com.wemake.market.domain.Coupon;
import com.wemake.market.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByItem(Item item);
}
