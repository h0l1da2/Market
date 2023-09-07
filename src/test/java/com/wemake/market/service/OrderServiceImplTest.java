package com.wemake.market.service;

import com.wemake.market.domain.*;
import com.wemake.market.domain.dto.ItemCreateDto;
import com.wemake.market.domain.dto.OrderItemDto;
import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.repository.ItemPriceHistoryRepository;
import com.wemake.market.repository.ItemRepository;
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

    @BeforeEach
    void clean() {
        itemPriceHistoryRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("결제 금액 계산 성공! : 아이템 하나, 쿠폰 사용 X")
    void 결제금액_성공_하나() throws ItemNotFoundException {
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
    void 결제금액_성공_하나_쿠폰_아이템_고정값() throws ItemNotFoundException {
        // given
        Item item = saveItem("감자", 1000);
        OrderItemDto orderItemDto = getOrderItemDto(item, 10);

        // 총 10000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto);

        Coupon coupon = Coupon.builder()
                .item(item)
                .how(How.FIXED)
                .wheres(Where.ITEM)
                .build();

        coupon.isFixedPrice(2000);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(9000);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 하나,무료")
    void 결제금액_성공_하나_쿠폰_아이템_고정값_무료() throws ItemNotFoundException {
        // given
        Item item = saveItem("감자", 1000);
        OrderItemDto orderItemDto = getOrderItemDto(item, 10);

        // 총 10000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto);

        Coupon coupon = Coupon.builder()
                .item(item)
                .how(How.FIXED)
                .wheres(Where.ITEM)
                .build();

        coupon.isFixedPrice(200000);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(0);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 하나,퍼센트")
    void 결제금액_성공_쿠폰_아이템_퍼센트_하나() throws ItemNotFoundException {
        // given
        Item item = saveItem("감자", 1000);
        OrderItemDto orderItemDto = getOrderItemDto(item, 10);

        // 총 10000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto);

        Coupon coupon = Coupon.builder()
                .item(item)
                .how(How.PERCENTAGE)
                .wheres(Where.ITEM)
                .build();
        coupon.isPercentagePrice(10);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(10000);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 하나,백퍼센트")
    void 결제금액_성공_쿠폰_아이템_퍼센트_하나_무료() throws ItemNotFoundException {
        // given
        Item item = saveItem("감자", 1000);
        OrderItemDto orderItemDto = getOrderItemDto(item, 10);

        // 총 10000
        List<OrderItemDto> list = new ArrayList<>();
        list.add(orderItemDto);

        Coupon coupon = Coupon.builder()
                .item(item)
                .how(How.PERCENTAGE)
                .wheres(Where.ITEM)
                .build();
        coupon.isPercentagePrice(100);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(1000);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 여러개,퍼센트")
    void 결제금액_성공_쿠폰_아이템_퍼센트_여러개() throws ItemNotFoundException {
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

        Coupon coupon = Coupon.builder()
                .item(item1)
                .how(How.PERCENTAGE)
                .wheres(Where.ITEM)
                .build();

        coupon.isPercentagePrice(40);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(23000);
    }


    // 과연 아이템이 중복으로 들어갔다면 ???
    // 옵션이 생길 경우... -> 이름만으로 비교하면 나중에 고칠 게 많아지려나
    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 주문,퍼센트,여러개")
    void 결제금액_성공_하나_쿠폰_주문_퍼센트_여러개() throws ItemNotFoundException {
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

        Coupon coupon = Coupon.builder()
                .item(item1)
                .how(How.PERCENTAGE)
                .wheres(Where.ORDER)
                .build();
        coupon.isPercentagePrice(20);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(21600);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 주문,퍼센트,여러개,100퍼")
    void 결제금액_성공_하나_쿠폰_주문_퍼센트_여러개_백퍼() throws ItemNotFoundException {
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

        Coupon coupon = Coupon.builder()
                .item(item1)
                .how(How.PERCENTAGE)
                .wheres(Where.ORDER)
                .build();

        coupon.isPercentagePrice(100);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(0);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 주문,고정값,여러개")
    void 결제금액_성공_하나_쿠폰_주문_고정값_여러개() throws ItemNotFoundException {
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

        Coupon coupon = Coupon.builder()
                .item(item1)
                .how(How.FIXED)
                .wheres(Where.ORDER)
                .build();

        coupon.isFixedPrice(3000);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(24000);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 주문,고정값,여러개, 무료")
    void 결제금액_성공_하나_쿠폰_주문_고정값_여러개_무료() throws ItemNotFoundException {
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

        Coupon coupon = Coupon.builder()
                .item(item1)
                .how(How.FIXED)
                .wheres(Where.ORDER)
                .build();

        coupon.isFixedPrice(3000000);

        OrderDto orderDto = getOrderDtoUserCoupon(list, 1000, coupon);

        // when
        int payPrice = orderService.getOrderPrice(orderDto);

        // then
        assertThat(payPrice).isEqualTo(0);
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
                .coupon(coupon)
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
                .id(item.getId())
                .count(count)
                .build();

        return orderItemDto;
    }
}