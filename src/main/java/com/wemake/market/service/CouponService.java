package com.wemake.market.service;

import com.wemake.market.domain.dto.CouponDto;
import com.wemake.market.domain.dto.CouponSaveDto;
import com.wemake.market.exception.DuplicateCouponException;
import com.wemake.market.exception.FormErrorException;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.exception.NotAuthorityException;

public interface CouponService {
    CouponDto saveCoupon(CouponSaveDto couponSaveDto) throws ItemNotFoundException, NotAuthorityException, DuplicateCouponException, FormErrorException;
}
