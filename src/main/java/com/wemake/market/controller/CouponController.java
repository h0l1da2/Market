package com.wemake.market.controller;

import com.wemake.market.domain.dto.CouponDto;
import com.wemake.market.domain.dto.CouponSaveDto;
import com.wemake.market.exception.DuplicateCouponException;
import com.wemake.market.exception.FormErrorException;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponDto> save(@RequestBody @Valid CouponSaveDto couponSaveDto) throws ItemNotFoundException, NotAuthorityException, DuplicateCouponException, FormErrorException {

        CouponDto couponDto = couponService.saveCoupon(couponSaveDto);

        return ResponseEntity.ok(couponDto);
    }
}
