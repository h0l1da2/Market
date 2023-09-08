package com.wemake.market.service;

import com.wemake.market.domain.*;
import com.wemake.market.domain.dto.CouponDto;
import com.wemake.market.domain.dto.CouponSaveDto;
import com.wemake.market.exception.*;
import com.wemake.market.repository.CouponRepository;
import com.wemake.market.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
public class CouponServiceImpl implements CouponService {

    @Value("${market.password}")
    private String password;
    private final CouponRepository couponRepository;
    private final ItemRepository itemRepository;
    @Override
    public CouponDto saveCoupon(CouponSaveDto couponSaveDto) throws ItemNotFoundException, DuplicateCouponException, FormErrorException, NotAuthorityException {

        rolePasswordCheck(couponSaveDto);
        couponSaveDtoFormCheck(couponSaveDto);

        Item item = null;
        Where userWhere = couponSaveDto.getWheres();
        Long itemId = couponSaveDto.getItemId();

        if (userWhere.equals(Where.ITEM) && itemId != null) {

            item = itemRepository.findById(couponSaveDto.getItemId()).orElseThrow(ItemNotFoundException::new);

            Coupon duplicateCoupon = couponRepository.findByItem(item).orElse(null);

            if (duplicateCoupon != null) {
                throw new DuplicateCouponException();
            }

        }
        Coupon coupon = Coupon.builder()
                .item(item)
                .how(couponSaveDto.getHow())
                .wheres(couponSaveDto.getWheres())
                .rate(couponSaveDto.getRate())
                .amount(couponSaveDto.getAmount())
                .build();

        Coupon saveCoupon = couponRepository.save(coupon);

        Item couponItem = coupon.getItem();
        itemId = couponItem != null ? couponItem.getId() : null;

        return CouponDto.builder()
                .couponId(saveCoupon.getId())
                .itemId(itemId)
                .wheres(saveCoupon.getWheres())
                .how(saveCoupon.getHow())
                .amount(saveCoupon.getAmount())
                .rate(saveCoupon.getRate())
                .build();
    }

    private void couponSaveDtoFormCheck(CouponSaveDto couponSaveDto) throws FormErrorException {
        if (couponSaveDto.getWheres() == null | couponSaveDto.getHow() == null) {
            throw new FormErrorException();
        }
        if (couponSaveDto.getHow() == How.PERCENTAGE && couponSaveDto.getRate() == 0) {
            throw new FormErrorException();
        }
        if (couponSaveDto.getHow() == How.FIXED && couponSaveDto.getAmount() == 0) {
            throw new FormErrorException();
        }
        if (couponSaveDto.getHow() == How.PERCENTAGE && couponSaveDto.getAmount() != 0) {
            throw new FormErrorException();
        }
        if (couponSaveDto.getHow() == How.FIXED && couponSaveDto.getRate() != 0) {
            throw new FormErrorException();
        }
    }

    private void rolePasswordCheck(CouponSaveDto couponSaveDto) throws NotAuthorityException {
        if (!couponSaveDto.getRole().equals(Role.MARKET) ||
                !couponSaveDto.getPassword().equals(password)) {
            throw new NotAuthorityException("권한 없음");
        }
    }

}
