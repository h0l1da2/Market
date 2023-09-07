package com.wemake.market.service;

import com.wemake.market.domain.*;
import com.wemake.market.domain.dto.CouponDto;
import com.wemake.market.domain.dto.CouponSaveDto;
import com.wemake.market.exception.DuplicateCouponException;
import com.wemake.market.exception.FormErrorException;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.repository.CouponRepository;
import com.wemake.market.repository.ItemPriceHistoryRepository;
import com.wemake.market.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

@SpringBootTest
public class CouponServiceImplTest {

    @Autowired
    private ItemPriceHistoryRepository itemPriceHistoryRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponRepository couponRepository;

    @BeforeEach
    void clean() {
        couponRepository.deleteAll();
        itemPriceHistoryRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("쿠폰 추가 아이템 : 성공 !")
    void 쿠폰_추가_성공() throws NotAuthorityException, DuplicateCouponException, ItemNotFoundException, FormErrorException {
        // given
        LocalDateTime createDate = LocalDateTime.of(
                2023, 1, 1, 12, 0, 0
        );

        Item item = Item.builder()
                .name("아이템")
                .date(createDate)
                .build();

        Item saveItem = itemRepository.save(item);

        ItemPriceHistory itemPriceHistory = ItemPriceHistory.builder()
                .item(saveItem)
                .date(saveItem.getDate())
                .price(1000)
                .build();

        ItemPriceHistory priceHistory = itemPriceHistoryRepository.save(itemPriceHistory);

        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .itemId(saveItem.getId())
                .how(How.FIXED)
                .wheres(Where.ITEM)
                .amount(1000)
                .build();

        // when
        CouponDto couponDto = couponService.saveCoupon(couponSaveDto);

        // then
        assertThat(couponDto.getCouponId()).isNotNull();
    }

    @Test
    @DisplayName("쿠폰 추가 실패 : 고정값인데 퍼센테이지 배율값에 값이 들어가있음")
    void 쿠폰_추가_실패_고정값인데_배율값이있다() {
        // given
        LocalDateTime createDate = LocalDateTime.of(
                2023, 1, 1, 12, 0, 0
        );

        Item item = Item.builder()
                .name("아이템")
                .date(createDate)
                .build();

        Item saveItem = itemRepository.save(item);

        ItemPriceHistory itemPriceHistory = ItemPriceHistory.builder()
                .item(saveItem)
                .date(saveItem.getDate())
                .price(1000)
                .build();

        ItemPriceHistory priceHistory = itemPriceHistoryRepository.save(itemPriceHistory);

        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .itemId(saveItem.getId())
                .how(How.FIXED)
                .wheres(Where.ITEM)
                .amount(1000)
                .rate(10)
                .build();

        // when then
        assertThrows(FormErrorException.class, () -> couponService.saveCoupon(couponSaveDto));

    }

    @Test
    @DisplayName("쿠폰 추가 실패 : 같은 아이템으로 또 쿠폰을 적용하려고 함")
    void 쿠폰_추가_실패_동일아이템_쿠폰추가불가() throws NotAuthorityException, FormErrorException, ItemNotFoundException, DuplicateCouponException {
        // given
        LocalDateTime createDate = LocalDateTime.of(
                2023, 1, 1, 12, 0, 0
        );

        Item item = Item.builder()
                .name("아이템")
                .date(createDate)
                .build();

        Item saveItem = itemRepository.save(item);

        ItemPriceHistory itemPriceHistory = ItemPriceHistory.builder()
                .item(saveItem)
                .date(saveItem.getDate())
                .price(1000)
                .build();

        ItemPriceHistory priceHistory = itemPriceHistoryRepository.save(itemPriceHistory);

        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .itemId(saveItem.getId())
                .how(How.FIXED)
                .wheres(Where.ITEM)
                .amount(1000)
                .build();

        couponService.saveCoupon(couponSaveDto);

        // when then
        assertThrows(DuplicateCouponException.class, () -> couponService.saveCoupon(couponSaveDto));

    }
    @Test
    @DisplayName("쿠폰 추가 실패 : 없는 상품으로 쿠폰 추가 시도")
    void 쿠폰_추가_실패_없는아이템으로_추가시도() {
        // given
        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .itemId(1L)
                .how(How.FIXED)
                .wheres(Where.ITEM)
                .amount(1000)
                .build();

        // when then
        assertThrows(ItemNotFoundException.class, () -> couponService.saveCoupon(couponSaveDto));

    }
    @Test
    @DisplayName("쿠폰 추가 성공 : 주문 쿠폰")
    void 쿠폰_추가_성공_주문쿠폰() throws NotAuthorityException, DuplicateCouponException, FormErrorException, ItemNotFoundException {
        // given
        CouponSaveDto couponSaveDto = CouponSaveDto.builder()
                .itemId(1L)
                .how(How.FIXED)
                .wheres(Where.ORDER)
                .amount(1000)
                .build();

        // when
        CouponDto saveCoupon = couponService.saveCoupon(couponSaveDto);

        // then
        assertThat(saveCoupon).isNotNull();
        assertThat(saveCoupon.getCouponId()).isNotNull();
        assertThat(saveCoupon.getRate()).isEqualTo(couponSaveDto.getRate());
        assertThat(saveCoupon.getWheres()).isEqualTo(couponSaveDto.getWheres());
        assertThat(saveCoupon.getHow()).isEqualTo(couponSaveDto.getHow());

    }
}
