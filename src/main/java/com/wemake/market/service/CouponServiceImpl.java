package com.wemake.market.service;

import com.wemake.market.domain.Coupon;
import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.dto.CouponDto;
import com.wemake.market.domain.dto.CouponSaveDto;
import com.wemake.market.exception.DuplicateCouponException;
import com.wemake.market.exception.DuplicateItemException;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.exception.NotAuthorityException;
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
    public CouponDto saveCoupon(CouponSaveDto couponSaveDto) throws ItemNotFoundException, NotAuthorityException, DuplicateCouponException {

        checkMarketRole(couponSaveDto.getRole(), couponSaveDto.getPassword());

        Item item = itemRepository.findById(couponSaveDto.getItemId()).orElseThrow(ItemNotFoundException::new);

        couponRepository.findByItem(item).orElseThrow(DuplicateCouponException::new);

        Coupon coupon = Coupon.builder()
                .item(item)
                .how(couponSaveDto.getHow())
                .wheres(couponSaveDto.getWheres())
                .rate(couponSaveDto.getRate())
                .amount(couponSaveDto.getAmount())
                .build();

        Coupon saveCoupon = couponRepository.save(coupon);

        return CouponDto.builder()
                .couponId(saveCoupon.getId())
                .itemId(item.getId())
                .wheres(saveCoupon.getWheres())
                .how(saveCoupon.getHow())
                .amount(saveCoupon.getAmount())
                .rate(saveCoupon.getRate())
                .build();
    }

    private void checkMarketRole(Role role, String pwd) throws NotAuthorityException {
        if (!role.equals(Role.MARKET) ||
                !pwd.equals(password)) {
            throw new NotAuthorityException("권한 없음 : 비밀번호 에러");
        }
    }

}
