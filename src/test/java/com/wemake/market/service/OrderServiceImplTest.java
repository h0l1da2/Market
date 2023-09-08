package com.wemake.market.service;

import com.wemake.market.domain.*;
import com.wemake.market.domain.dto.ItemCreateDto;
import com.wemake.market.domain.dto.OrderItemDto;
import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.exception.CouponErrorException;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.repository.CouponRepository;
import com.wemake.market.repository.ItemPriceHistoryRepository;
import com.wemake.market.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemPriceHistoryRepository itemPriceHistoryRepository;
    @Autowired
    private CouponRepository couponRepository;

    @BeforeEach
    void clean() {
        couponRepository.deleteAll();
        itemPriceHistoryRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("결제 금액 계산 성공! : 아이템 하나, 쿠폰 사용 X")
    void 결제금액_성공_하나() throws ItemNotFoundException, CouponErrorException {
        // given
        Item item = saveItem("감자", 1000);
        OrderItemDto orderItemDto = getOrderItemDto(item, 10);

        // 총 10000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto);

        OrderDto orderDto = OrderDto
                .builder()
                .items(list)
                .deliveryPrice(1000)
                .useCoupon(false)
                .build();

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(11000);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 하나,고정값")
    void 결제금액_성공_하나_쿠폰_아이템_고정값() throws ItemNotFoundException, CouponErrorException {
        // given
        Item item = saveItem("감자", 1000);
        OrderItemDto orderItemDto = getOrderItemDto(item, 10);

        // 총 10000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto);

        // 1000
        Coupon coupon = getFixedItemCoupon(item);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(1000);
    }
    @Test
    @DisplayName("결제 금액 계산 쿠폰 실패! : 쿠폰이 해당 아이템에 해당하지 않음")
    void 결제금액_성공_하나_쿠폰_아이템해당없음() {
        // given
        Item item1 = saveItem("감자", 1000);
        OrderItemDto orderItemDto = getOrderItemDto(item1, 10);

        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto);

        Item item2 = saveItem("고구마", 1000);
        Coupon coupon = getFixedItemCoupon(item2);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when then
        Assertions.assertThrows(CouponErrorException.class, () ->
                orderService.getOrderPrice(orderDto));

    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 하나,무료")
    void 주문_성공_하나_쿠폰_아이템_고정값_무료() throws ItemNotFoundException, CouponErrorException {
        // given
        Item item = saveItem("감자", 1000);
        OrderItemDto orderItemDto = getOrderItemDto(item, 10);

        // 총 10000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto);

        // 1000
        Coupon coupon = getFixedItemCoupon(item);


        coupon.isFixedPrice(1000);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(1000);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 하나,퍼센트")
    void 결제금액_성공_쿠폰_아이템_퍼센트_하나() throws ItemNotFoundException, CouponErrorException {
        // given
        Item item = saveItem("감자", 1000);
        OrderItemDto orderItemDto = getOrderItemDto(item, 10);

        // 총 10000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto);

        // 10 퍼
        Coupon coupon = getPercentageItemCoupon(item);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 2000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(11000);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 하나,백퍼센트")
    void 결제금액_성공_쿠폰_아이템_퍼센트_하나_무료() throws ItemNotFoundException, CouponErrorException {
        // given
        Item item = saveItem("감자", 1000);
        OrderItemDto orderItemDto = getOrderItemDto(item, 10);

        // 총 10000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto);

        // 10 퍼
        Coupon coupon = getPercentageItemCoupon(item);
        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(10000);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 여러개,퍼센트")
    void 결제금액_성공_쿠폰_아이템_퍼센트_여러개() throws ItemNotFoundException, CouponErrorException {
        // given
        // 10000
        Item item1 = saveItem("감자", 1000);
        OrderItemDto orderItemDto1 = getOrderItemDto(item1, 10);

        // 12000
        Item item2 = saveItem("고구마", 3000);
        OrderItemDto orderItemDto2 = getOrderItemDto(item2, 4);

        // 4000
        Item item3 = saveItem("치즈", 2000);
        OrderItemDto orderItemDto3 = getOrderItemDto(item3, 2);

        // 총 26000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);

        // 10퍼
        Coupon coupon = getPercentageItemCoupon(item1);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(26000);
    }
    @Test
    @DisplayName("결제 금액 계산 쿠폰 실패! : 아이템 여러개,다른 쿠폰")
    void 결제금액_실패_다른쿠폰_아이템_여러개() {
        // given
        // 10000
        Item item1 = saveItem("감자", 1000);
        OrderItemDto orderItemDto1 = getOrderItemDto(item1, 10);

        // 12000
        Item item2 = saveItem("고구마", 3000);
        OrderItemDto orderItemDto2 = getOrderItemDto(item2, 4);

        // 4000
        Item item3 = saveItem("치즈", 2000);
        OrderItemDto orderItemDto3 = getOrderItemDto(item3, 2);

        // 총 26000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);

        // 10퍼
        Item item4 = saveItem("이상한놈", 2000);
        Coupon coupon = getPercentageItemCoupon(item4);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when then
        Assertions.assertThrows(CouponErrorException.class, () ->
                orderService.getOrderPrice(orderDto));
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 여러개,고정")
    void 결제금액_성공_쿠폰_아이템_고정_여러개() throws ItemNotFoundException, CouponErrorException {
        // given
        // 10000
        Item item1 = saveItem("감자", 1000);
        OrderItemDto orderItemDto1 = getOrderItemDto(item1, 10);

        // 12000
        Item item2 = saveItem("고구마", 3000);
        OrderItemDto orderItemDto2 = getOrderItemDto(item2, 4);

        // 4000
        Item item3 = saveItem("치즈", 2000);
        OrderItemDto orderItemDto3 = getOrderItemDto(item3, 2);

        // 총 26000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);

        // 3000
        Coupon coupon = getFixedOrderCoupon(item1);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(24000);
    }



    // 과연 아이템이 중복으로 들어갔다면 ???
    // 옵션이 생길 경우... -> 이름만으로 비교하면 나중에 고칠 게 많아지려나
    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 주문,퍼센트,여러개")
    void 결제금액_성공_하나_쿠폰_주문_퍼센트_여러개() throws ItemNotFoundException, CouponErrorException {
        // given
        // 10000
        Item item1 = saveItem("감자", 1000);
        OrderItemDto orderItemDto1 = getOrderItemDto(item1, 10);

        // 12000
        Item item2 = saveItem("고구마", 3000);
        OrderItemDto orderItemDto2 = getOrderItemDto(item2, 4);

        // 4000
        Item item3 = saveItem("치즈", 2000);
        OrderItemDto orderItemDto3 = getOrderItemDto(item3, 2);

        // 총 26000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);

        // 20퍼
        Coupon coupon = getOrderPercentageCoupon(item1);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(21600);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 주문,퍼센트,여러개,100퍼")
    void 결제금액_성공_하나_쿠폰_주문_퍼센트_여러개_백퍼() throws ItemNotFoundException, CouponErrorException {
        // given
        // 10000
        Item item1 = saveItem("감자", 1000);
        OrderItemDto orderItemDto1 = getOrderItemDto(item1, 10);

        // 12000
        Item item2 = saveItem("고구마", 3000);
        OrderItemDto orderItemDto2 = getOrderItemDto(item2, 4);

        // 4000
        Item item3 = saveItem("치즈", 2000);
        OrderItemDto orderItemDto3 = getOrderItemDto(item3, 3);

        // 총 26000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);

        // 20퍼
        Coupon coupon = getOrderPercentageCoupon(item1);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 2000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(24000);
    }
    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 주문,고정값,여러개")
    void 결제금액_성공_하나_쿠폰_주문_고정값_여러개() throws ItemNotFoundException, CouponErrorException {
        // given
        // 10000
        Item item1 = saveItem("감자", 1000);
        OrderItemDto orderItemDto1 = getOrderItemDto(item1, 10);

        // 12000
        Item item2 = saveItem("고구마", 3000);
        OrderItemDto orderItemDto2 = getOrderItemDto(item2, 4);

        // 4000
        Item item3 = saveItem("치즈", 2000);
        OrderItemDto orderItemDto3 = getOrderItemDto(item3, 2);

        // 총 26000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);

        // 3000
        Coupon coupon = getFixedOrderCoupon(item1);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(24000);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 주문,고정값,여러개, 무료")
    void 결제금액_성공_하나_쿠폰_주문_고정값_여러개_무료() throws ItemNotFoundException, CouponErrorException {
        // given
        // 10000
        Item item1 = saveItem("감자", 1000);
        OrderItemDto orderItemDto1 = getOrderItemDto(item1, 10);

        // 12000
        Item item2 = saveItem("고구마", 3000);
        OrderItemDto orderItemDto2 = getOrderItemDto(item2, 4);

        // 4000
        Item item3 = saveItem("치즈", 2000);
        OrderItemDto orderItemDto3 = getOrderItemDto(item3, 2);

        // 총 26000

        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);

        // 3000
        Coupon coupon = getFixedOrderCoupon(item1);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(24000);
    }

    private Coupon getFixedOrderCoupon(Item item1) {
        Coupon coupon = Coupon.builder()
                .item(item1)
                .how(How.FIXED)
                .wheres(Where.ORDER)
                .amount(3000)
                .build();

        coupon = couponRepository.save(coupon);

        return coupon;
    }

    private Coupon getOrderPercentageCoupon(Item item) {
        Coupon coupon = Coupon.builder()
                .item(item)
                .how(How.PERCENTAGE)
                .wheres(Where.ORDER)
                .rate(20)
                .build();

        couponRepository.save(coupon);

        return coupon;
    }

    private Coupon getFixedItemCoupon(Item item) {
        Coupon coupon = Coupon.builder()
                .item(item)
                .how(How.FIXED)
                .wheres(Where.ITEM)
                .amount(1000)
                .build();

        coupon = couponRepository.save(coupon);

        return coupon;
    }

    private Item saveItem(String name, int price) {

        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name(name)
                .price(price)
                .role(Role.MARKET)
                .build();

        Item item = itemSave(itemCreateDto);

        return item;
    }

    private OrderDto getOrderDtoUserCoupon(List<OrderItemDto> list, int deliveryPrice, Coupon coupon) {

        OrderDto orderDto = OrderDto.builder()
                .items(list)
                .deliveryPrice(deliveryPrice)
                .useCoupon(true)
                .couponId(coupon.getId())
                .build();

        return orderDto;
    }

    private Item itemSave(ItemCreateDto itemCreateDto) {
        Item item = Item.builder()
                .name(itemCreateDto.getName())
                .build();
        Item saveItem = itemRepository.save(item);

        ItemPriceHistory itemPriceHistory = ItemPriceHistory.builder()
                .item(saveItem)
                .date(itemCreateDto.getDate())
                .price(itemCreateDto.getPrice())
                .build();
        itemPriceHistoryRepository.save(itemPriceHistory);

        return saveItem;

    }

    private OrderItemDto getOrderItemDto(Item item, int count) {

        OrderItemDto orderItemDto = OrderItemDto.builder()
                .itemId(item.getId())
                .count(count)
                .build();

        return orderItemDto;
    }

    private Coupon getPercentageItemCoupon(Item item) {
        Coupon coupon = Coupon.builder()
                .item(item)
                .how(How.PERCENTAGE)
                .wheres(Where.ITEM)
                .rate(10)
                .build();

        coupon = couponRepository.save(coupon);
        return coupon;
    }
}